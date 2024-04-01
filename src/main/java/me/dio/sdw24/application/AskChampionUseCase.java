package me.dio.sdw24.application;

import me.dio.sdw24.domain.exceptions.ChampionNotFoundException;
import me.dio.sdw24.domain.model.Champions;
import me.dio.sdw24.domain.ports.ChampionsRepository;
import me.dio.sdw24.domain.ports.GenerativeAiApi;

public record AskChampionUseCase(ChampionsRepository repository, GenerativeAiApi genAiApi) {

    public String askChampion(Long championId, String question) {

        Champions champion = repository.findById(championId)
                .orElseThrow(() -> new ChampionNotFoundException(championId));

        String championContext = champion.generateContextByQuestion(question);
        String objective = """
                seu objetivo é responder perguntas atuando como um campeão do league of legends,
                responda perguntas incorporando a personalidade e estilo do campeão.
                Segue a pergunta, o nome do campeão e a sua história:
                
                """;

        return genAiApi.generateContent(objective, championContext);
    }

}
