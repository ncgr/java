package org.ncgr.chatbot.pinecone;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import io.pinecone.PineconeClient;
import io.pinecone.PineconeClientConfig;
import io.pinecone.PineconeConnection;
import io.pinecone.PineconeConnectionConfig;
import io.pinecone.PineconeException;

import io.pinecone.proto.DeleteRequest;
import io.pinecone.proto.DeleteResponse;
import io.pinecone.proto.FetchRequest;
import io.pinecone.proto.FetchResponse;
import io.pinecone.proto.QueryRequest;
import io.pinecone.proto.QueryResponse;
import io.pinecone.proto.QueryVector;
import io.pinecone.proto.SingleQueryResults;
import io.pinecone.proto.ScoredVector;
import io.pinecone.proto.UpdateRequest;
import io.pinecone.proto.UpdateResponse;
import io.pinecone.proto.UpsertRequest;
import io.pinecone.proto.UpsertResponse;
import io.pinecone.proto.Vector;

/**
 * Class holding a PineconeClient and PineconeConnection with methods to perform Pinecone operations.
 */
public class Pinecone {
    // default parameters
    public static final int TOP_K = 5;
    public static final int SERVER_SIDE_TIMEOUT_SEC = 30;

    // Limit the character length of individual abstracts. Most abstracts are around 1500 chars.
    public static final int ABSTRACT_LIMIT = 2000;
    
    PineconeClient client;
    PineconeConnection connection;

    /**
     * Instantiate by creating the client and connection with a given API key and other parameters.
     */
    public Pinecone(String projectName, String apiKey, String environment, String indexName, int serverSideTimeoutSec) {
        PineconeClientConfig clientConfig = new PineconeClientConfig()
            .withApiKey(apiKey)
            .withEnvironment(environment)
            .withProjectName(projectName)
            .withServerSideTimeoutSec(serverSideTimeoutSec);
        PineconeConnectionConfig connectionConfig = new PineconeConnectionConfig()
            .withIndexName(indexName);
        this.client = new PineconeClient(clientConfig);
        this.connection = client.connect(connectionConfig);
    }

    /**
     * Utility to get metadat from a Vector in the form of a Map.
     */
    public static Map<String,String> getMetadata(Vector vector) {
	Map<String,String> metadataMap = new HashMap<>();
        Map<String,Value> fieldsMap = vector.getMetadata().getFieldsMap();
	for (String key : fieldsMap.keySet()) {
	    metadataMap.put(key, fieldsMap.get(key).getStringValue());
        }
	return metadataMap;
    }

    /**
     * Utility to get metadat from a ScoredVector in the form of a Map.
     */
    public static Map<String,String> getMetadata(ScoredVector vector) {
	Map<String,String> metadataMap = new HashMap<>();
        Map<String,Value> fieldsMap = vector.getMetadata().getFieldsMap();
	for (String key : fieldsMap.keySet()) {
	    metadataMap.put(key, fieldsMap.get(key).getStringValue());
        }
	return metadataMap;
    }

    /**
     * Utility to get a metadata value for a given key from a Vector, null if it doesn't exist.
     */
    public static String getMetadataValue(Vector vector, String key) {
        Map<String,Value> fieldsMap = vector.getMetadata().getFieldsMap();
        if (fieldsMap.get(key) == null) {
            return null;
        } else {
            return fieldsMap.get(key).getStringValue();
        }
    }

    /**
     * Utility to get a metadata value for a given key from a ScoredVector, null if it doesn't exist.
     */
    public static String getMetadataValue(ScoredVector vector, String key) {
        Map<String,Value> fieldsMap = vector.getMetadata().getFieldsMap();
        if (fieldsMap.get(key) == null) {
            return null;
        } else {
            return fieldsMap.get(key).getStringValue();
        }
    }

    /**
     * Create string contexts from a list of ScoredVectors from Pinecone.
     * This method assumes that the Vectors contain publication abstracts with appropriate metadata including "abstract".
     * Requires an instantiated connection.
     *
     * @param scoredVectors a List of Pinecone ScoredVectors
     * @return a List of String contexts
     */
    public List<String> getAbstractContexts(List<ScoredVector> vectors) throws PineconeException {
        List<String> contexts = new ArrayList<>();
        for (ScoredVector vector : vectors) {
            contexts.add(getAbstractContext(vector));
        }
        return contexts;
    }

    /**
     * Return a text Context from a single Pinecone ScoredVector metadata, assuming it is built to contain a publication abstract with metadata.
     * Returns null if the ScoredVector metadata does not contain "abstract".
     * The other metadata is appended to the abstract in JSON format:
     * {
     *   "title": "Genotype delimitation in the Nod-independent model legume Aeschynomene evenia.",
     *   "DOI": "10.1371/journal.pone.0063836",
     *   "PMID": "23717496",
     *   "PMCID": "PMC3662760",
     * }
     * The returned context is trimmed to ABSTRACT_LIMIT.
     *
     * @param vector a Pinecone ScoredVector
     * @return a String context, or null if the vector doesn't contain an "abstract" metadata item
     */
    public String getAbstractContext(ScoredVector vector) {
        // Google protocol buffer Struct
        Map<String,Value> fieldsMap = vector.getMetadata().getFieldsMap();
        if (fieldsMap.get("abstract") == null) return null;
        String context = fieldsMap.get("abstract").getStringValue();
        // trim context if too long
        if (context.length() > ABSTRACT_LIMIT) {
            context = context.substring(0, ABSTRACT_LIMIT);
        }
        context += "\n{";
        if (fieldsMap.get("title") != null) context += "Title: " + fieldsMap.get("title").getStringValue() + ", ";
        // we don't use keywords so omit to save on size
        // if (fieldsMap.get("keywords") != null) context += "Keywords: " + fieldsMap.get("keywords").getStringValue() + ", ";
        if (fieldsMap.get("DOI") != null) context += "DOI: " + fieldsMap.get("DOI").getStringValue() + ", ";
        if (fieldsMap.get("PMID") != null) context += "PMID: " + fieldsMap.get("PMID").getStringValue() + ", ";
        if (fieldsMap.get("PMCID") != null) context += "PMCID: " + fieldsMap.get("PMCID").getStringValue() + ", ";
        context += "}";
        return context;
    }

    /**
     * Retrieve a List of Pinecone ScoredVector that match the given encoded query embedding.
     * Requires an instantiated connection.
     *
     * @param encodedQuery the Float List query vector
     * @param topK the top_k value in the QueryVector and QueryRequest.
     * @param includeValues indicates whether to include the embedding values in the ScoredVectors
     * @param includeMetadata indicates whether to include the Vector metadata
     * @return a List of ScoredVector
     */
    public List<ScoredVector> getScoredVectors(List<Float> encodedQuery, int topK, boolean includeValues, boolean includeMetadata) throws PineconeException {
        QueryVector queryVector = QueryVector
            .newBuilder()
            .setTopK(topK)
            .addAllValues(encodedQuery)
            .build();
        QueryRequest request = QueryRequest
            .newBuilder()
            .setIncludeValues(includeValues)
            .setIncludeMetadata(includeMetadata)
            .setTopK(topK)
            .addQueries(queryVector)
            .build();
        QueryResponse queryResponse = connection.getBlockingStub().query(request);
        SingleQueryResults results = queryResponse.getResults(0);
        return results.getMatchesList();
    }

    /**
     * Make a Pinecone metadata filter that equates a key with value.
     */
    public static Struct makeEqFilter(String key, String value) {
        return Struct
            .newBuilder()
            .putFields(key,
                       Value
                       .newBuilder()
                       .setStructValue(Struct
                                       .newBuilder()
                                       .putFields("$eq",
                                                  Value
                                                  .newBuilder()
                                                  .setStringValue(value)
                                                  .build())
                                       .build())
                       .build())
            .build();
    }

    /**
     * Retrieve a List of Pinecone ScoredVector that match the given encoded query embedding and applying a filter.
     * Requires an instantiated connection.
     *
     * @param encodedQuery the Float List query vector
     * @param topK the top_k value in the QueryVector and QueryRequest.
     * @param includeValues indicates whether to include the embedding values in the ScoredVectors
     * @param includeMetadata indicates whether to include the Vector metadata
     * @return a List of ScoredVector
     */
    public List<ScoredVector> getScoredVectorsWithFilter(List<Float> encodedQuery, Struct filter,
                                                         int topK, boolean includeValues, boolean includeMetadata) throws PineconeException {
        QueryVector queryVector = QueryVector
            .newBuilder()
            .setTopK(topK)
            .addAllValues(encodedQuery)
            .build();
        QueryRequest request = QueryRequest
            .newBuilder()
            .setIncludeValues(includeValues)
            .setIncludeMetadata(includeMetadata)
            .setTopK(topK)
            .setFilter(filter)
            .addQueries(queryVector)
            .build();
        QueryResponse queryResponse = connection.getBlockingStub().query(request);
        SingleQueryResults results = queryResponse.getResults(0);
        return results.getMatchesList();
    }
    
    /**
     * Upsert a List of Vectors.
     * Requires an instantiated instance connection.
     * To avoid limits at Pinecone, we upsert max 100 at a time.
     */
    public void upsertVectors(List<Vector> vectors) throws PineconeException {
        int start = 0;
        int end = Math.min(100, vectors.size());
        boolean hasMore = true;
        while (hasMore) {
            List<Vector> subList = vectors.subList(start, end);
            UpsertRequest upsertRequest = UpsertRequest
                .newBuilder()
                .addAllVectors(subList)
                .build();
            UpsertResponse response = connection.getBlockingStub().upsert(upsertRequest);
            // increment sub-list indexes
            start = Math.min(start + 100, vectors.size());
            end = Math.min(end + 100, vectors.size());
            hasMore = start < vectors.size();
        }
    }
    
    /**
     * Delete a list of Vectors identified by a List of IDs.
     * Requires an instantiated instance connection.
     */
    public void deleteVectors(List<String> ids) throws PineconeException {
        DeleteRequest request = DeleteRequest
            .newBuilder()
            .setDeleteAll(false)
            .addAllIds(ids)
            .build();
        DeleteResponse response = connection.getBlockingStub().delete(request);
    }

    /**
     * Fetch Vectors for a List of IDs. Vectors will include values and metadata.
     * Requires an instantiated instance connection.
     * NOTE: a maximum of 1000 Vectors may be requested at a time, so we chunk.
     *
     * @param ids a List of Vector IDs
     * @return a Map of Vectors keyed by ID
     */
    public Map<String,Vector> fetchVectors(List<String> ids) throws PineconeException {
        int start = 0;
        int end = Math.min(1000, ids.size());
        boolean hasMore = true;
        Map<String,Vector> vectorsMap = new HashMap<>();
        while (hasMore) {
            List<String> subList = ids.subList(start, end);
            FetchRequest request = FetchRequest
                .newBuilder()
                .addAllIds(subList)
                .build();
            try {
                // this can be unreliable so wrap in try to repeat if it fails
                FetchResponse response = connection.getBlockingStub().fetch(request);
                Map<String,Vector> vectorsSubMap = response.getVectorsMap();
                for (String id : vectorsSubMap.keySet()) {
                    vectorsMap.put(id, vectorsSubMap.get(id));
                }
                // increment sub-list indexes
                start = Math.min(start + 1000, ids.size());
                end = Math.min(end + 1000, ids.size());
                hasMore = start < ids.size();
            } catch (io.grpc.StatusRuntimeException ex) {
                // signal exception and repeat
                System.err.println("### " + ex.getMessage());
            }
        }
        return vectorsMap;
    }

    /**
     * Update the metadata for a Vector given by id with the given key and value.
     * Requires an instantiated instance connection.
     */
    public void updateVector(String id, String key, String value) throws PineconeException {
        UpdateRequest request = UpdateRequest
            .newBuilder()
            .setId(id)
            .setSetMetadata(Struct
                            .newBuilder()
                            .putFields(key,
                                       Value
                                       .newBuilder()
                                       .setStringValue(value)
                                       .build())
                            .build())
            .build();
        UpdateResponse response = connection.getBlockingStub().update(request);
    }

}
