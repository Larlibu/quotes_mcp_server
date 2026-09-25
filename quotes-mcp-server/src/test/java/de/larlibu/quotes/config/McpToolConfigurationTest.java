package de.larlibu.quotes.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.larlibu.quotes.service.QuoteService;
import de.larlibu.quotes.tools.QuoteTool;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;

class McpToolConfigurationTest {

    @Test
    void shouldExposeToolCallbacksFromQuoteTool() {
        QuoteService quoteService = new QuoteService() {
            @Override
            public de.larlibu.quotes.model.Quote save(de.larlibu.quotes.model.Quote quote) {
                return quote;
            }

            @Override
            public java.util.List<de.larlibu.quotes.model.Quote> searchQuotes(String query) {
                return java.util.List.of();
            }

            @Override
            public de.larlibu.quotes.model.Quote getRandomQuote(String autorTag, String themenTag, String sprachenTag) {
                return null;
            }

            @Override
            public boolean deleteQuote(String id) {
                return false;
            }

            @Override
            public java.util.Set<String> getAllCategories() {
                return java.util.Set.of();
            }

            @Override
            public int batchImport(java.util.List<de.larlibu.quotes.model.Quote> quotes) {
                return 0;
            }
        };

        QuoteTool quoteTool = new QuoteTool(quoteService);
        McpToolConfiguration configuration = new McpToolConfiguration();
        ToolCallbackProvider callbackProvider = configuration.quoteToolCallbackProvider(quoteTool);

        assertTrue(callbackProvider.getToolCallbacks().length > 0);
    }
}
