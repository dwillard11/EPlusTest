package ca.manitoulin.mtd.dao.maintenance;

import ca.manitoulin.mtd.dto.support.AppEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BusinessCodeMapper {
    List<AppEnum> selectBusinessCode(@Param("value")String name);
    AppEnum getEpCodeById(@Param("key")String key);
    List<AppEnum> getEpCodeListByType(@Param("key")String name, @Param("language")String language);

    List<String> selectCodeCategory();

    void insertBusinessCode(AppEnum appEnum);

    void deleteBusinessCode(@Param("id") Integer id);

    void updateBusinessCode(AppEnum appEnum);

    List<AppEnum> getCountry();

    List<AppEnum> getProvinceByCountry(@Param("country")String country);

    AppEnum getMultipileLanguageByText(@Param("textStr")String textStr);

    List<AppEnum> getEntityContactCountry();
}
