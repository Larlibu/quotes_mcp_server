package de.larlibu.quotes.config;

import de.larlibu.quotes.tools.QuoteTool;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.StaticToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registriert die MCP-Tool-Callbacks fuer den stdio MCP-Server.
 */
@Configuration
public class McpToolConfiguration {

    /**
     * Stellt die Tool-Methoden aus {@link QuoteTool} als MCP-Callbacks bereit.
     *
     * @param quoteTool Tool-Bean mit {@code @Tool}-Methoden
     * @return Provider fuer die MCP Tool-Registrierung
     */
    @Bean
    public ToolCallbackProvider quoteToolCallbackProvider(QuoteTool quoteTool) {
        return new StaticToolCallbackProvider(ToolCallbacks.from(quoteTool));
    }
}
