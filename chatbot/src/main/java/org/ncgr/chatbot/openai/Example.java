package org.ncgr.chatbot.openai;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.image.CreateImageRequest;

/**
 * Test the OpenAI API using the example given in
 * https://github.com/TheoKanning/openai-java/blob/main/example/src/main/java/example/OpenAiApiExample.java
 */
public class Example {

    public static void main(String[] args) {

        String token = System.getenv("OPENAI_API_KEY");
        OpenAiService service = new OpenAiService(token);

        System.out.println("");
        System.out.println("Creating completion...");
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("ada")
                .prompt("Somebody once told me the world is gonna roll me")
                .echo(true)
                .user("testing")
                .n(3)
                .build();
        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);

        System.out.println("");
        System.out.println("Creating Image...");
        CreateImageRequest request = CreateImageRequest.builder()
                .prompt("A cow breakdancing with a turtle")
                .build();

        System.out.println("Image is located at:");
        System.out.println(service.createImage(request).getData().get(0).getUrl());
    }

    public boolean someLibraryMethod() {
        return true;
    }
}
