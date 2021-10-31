import com.google.cloud.translate.*;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.Arrays;
import java.util.List;

// Basiert auf https://cloud.google.com/translate/docs/basic/translating-text#translate_translate_text-java
public class TranslateText {

    static List<Language> supportedTargetLanguages;
    static BidiMap<String, String> supportedTargetLanguagesHashMap = new DualHashBidiMap<>();
    static String targetLanguage = "de";

    public static void setTargetLanguage(String newTargetLanguageCode) {
        targetLanguage = newTargetLanguageCode;
    }

    public static List<Language> getSupportedTargetLanguages(){
        return supportedTargetLanguages;
    }

    public static String[] getSupportedTargetLanguageNames(){
        String[] result = supportedTargetLanguagesHashMap.keySet().toArray(new String[supportedTargetLanguagesHashMap.size()]);
        Arrays.sort(result);
        return result;
    }

    public static String[] getSupportedTargetLanguageCodes(){
        return supportedTargetLanguagesHashMap.values().toArray(new String[supportedTargetLanguagesHashMap.size()]);
    }

    public static String getSupportedTargetLanguageCode(String languageName) {
        return supportedTargetLanguagesHashMap.get(languageName);
    }

    public static String getSupportedTargetLanguageName(String languageCode) {
        return supportedTargetLanguagesHashMap.getKey(languageCode);
    }

    public static void setSupportedTargetLanguages(){
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        supportedTargetLanguages = translate.listSupportedLanguages(Translate.LanguageListOption.targetLanguage("de"));
        for (Language language : supportedTargetLanguages) {
            supportedTargetLanguagesHashMap.put(language.getName(), language.getCode());
        }
    }

    public static String translateText(String text){
        String retVal = "";
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translation translation = translate.translate(text, Translate.TranslateOption.targetLanguage(targetLanguage));
        retVal = translation.getTranslatedText().replace("&quot;", "''");
        return retVal;
    }
}
