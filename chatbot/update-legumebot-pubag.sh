#!/bin/bash
for species in "Aeschynomene evenia" \
                   "Apios americana" \
                   "Arachis cardenasii" \
                   "Arachis duranensis" \
                   "Arachis hypogaea" \
                   "Arachis ipaensis" \
                   "Arachis stenosperma" \
                   "Bauhinia blakeana" \
                   "Bauhinia purpurea" \
                   "Bauhinia tomentosa" \
                   "Bauhinia variegata" \
                   "Cajanus cajan" \
                   "Cercis canadensis" \
                   "Cercis gigantea" \
                   "Chamaecrista fasciculata" \
                   "Cicer arietinum" \
                   "Cicer echinospermum" \
                   "Cicer reticulatum" \
                   "Faidherbia albida" \
                   "Glycine cyrtoloba" \
                   "Glycine tomentella-D3" \
                   "Glycine dolichocarpa" \
                   "Glycine falcata" \
                   "Glycine max" \
                   "Glycine soja" \
                   "Glycine stenophita" \
                   "Glycine syndetika" \
		   "Lens culinaris" \
                   "Lens ervoides" \
                   "Lotus japonicus" \
                   "Lupinus albus" \
                   "Lupinus angustifolius" \
                   "Medicago polymorpha" \
                   "Medicago ruthenica" \
                   "Medicago sativa" \
                   "Medicago truncatula" \
                   "Phaseolus acutifolius" \
                   "Phaseolus coccineus" \
                   "Phaseolus debouckii" \
                   "Phaseolus dumosus" \
                   "Phaseolus lunatus" \
                   "Phaseolus vulgaris" \
                   "Pisum sativum" \
                   "Trifolium pratense" \
                   "Trifolium subterraneum" \
                   "Vicia faba" \
                   "Vigna angularis" \
                   "Vigna radiata" \
                   "Vigna unguiculata"
do
    echo $species
    scripts/pubag-embeddings-upserter.sh -i legumebot -u -t "$species"
done
