package ca.manitoulin.mtd.dao.maintenance;

import ca.manitoulin.mtd.dto.maintenance.QuoteTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuoteTemplateMapper {
    List<QuoteTemplate> selectQuoteTemplateListByType(@Param("tripType") String tripType, @Param("name") String name, @Param("language") String language);

    List<QuoteTemplate> selectQuoteTemplates(@Param("tripType") String tripType, @Param("name") String name);

    QuoteTemplate selectQuoteTemplateById(@Param("id") Integer id);

    void insertQuoteTemplate(QuoteTemplate quoteTemplate);

    void deleteQuoteTemplateByCategory(@Param("tripType") String tripType, @Param("name") String name);

    void deleteQuoteTemplate(@Param("id") Integer id);

    void updateQuoteTemplate(QuoteTemplate quoteTemplate);

    void updateQuoteTemplateCategorySequence(QuoteTemplate quoteTemplate);

    void updateQuoteTemplateSequence(QuoteTemplate quoteTemplate);

    Integer getCategorySequenceByCategory(@Param("name") String name, @Param("category") String category);

    Integer getLastCategorySequenceByName(@Param("name") String name, @Param("id") Integer id);

    Integer getItemSequenceByCategory(@Param("name") String name, @Param("category") String category);
}
