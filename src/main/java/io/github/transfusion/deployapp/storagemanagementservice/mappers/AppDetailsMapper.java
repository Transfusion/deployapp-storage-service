package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.transfusion.app_info_java_graalvm.AppInfo.APK;
import io.github.transfusion.app_info_java_graalvm.AppInfo.IPA;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Apk;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.ApkCert;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.Ipa;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Used to map FROM polyglot values into entities or other DeployApp DTOs
 */
@Mapper(
        componentModel = "spring"
)
public abstract class AppDetailsMapper {

    Logger logger = LoggerFactory.getLogger(AppDetailsMapper.class);

    @Autowired
    private Jackson2ObjectMapperBuilder mapperBuilder;

    @Named("infoPlistToJsonNode")
    JsonNode infoPlistToJsonNode(IPA ipa) throws JsonProcessingException {
        ObjectMapper mapper = mapperBuilder.build();
        Context ctx = ipa.getContext();
        Value infoPlistJson = ctx.eval("ruby", "-> recv { recv.to_h.to_json }");
        Value res = infoPlistJson.execute(ipa.info().getValue());
//        Value res = ipa.info().getValue().getMember("to_hash").getMember("to_json");
        logger.info(String.format("serialized info plist of %s", ipa.app_path()));
        logger.info(res.asString());
        return mapper.readTree(res.asString());
    }

    // map the base class AppBinary fields first before the Ipa-specific ones
    @Mapping(target = "id", source = "id")
    @Mapping(target = "version", expression = "java( ipa.release_version() )")
    @Mapping(target = "build", expression = "java( ipa.build_version() )")
    @Mapping(target = "uploadDate", expression = "java( java.time.Instant.now() )")
    @Mapping(target = "name", expression = "java( ipa.name() )")
    @Mapping(target = "identifier", expression = "java( ipa.identifier() )")
    @Mapping(target = "sizeBytes", expression = "java( java.math.BigDecimal.valueOf(ipa.size()) )")
    @Mapping(target = "fileName", source = "fileName")

    @Mapping(target = "storageCredential", source = "storageCredentialId")

    // then do Ipa-specific fields
    @Mapping(target = "minSdkVersion", expression = "java( ipa.min_sdk_version() )")
    @Mapping(target = "iphone", expression = "java( ipa.iphone() )")
    @Mapping(target = "ipad", expression = "java( ipa.ipad() )")
    @Mapping(target = "universal", expression = "java( ipa.universal() )")
    @Mapping(target = "deviceType", expression = "java( ipa.device_type() )")
    @Mapping(target = "archs", expression = "java( ipa.archs() == null ? null : java.util.Arrays.asList(ipa.archs()) )")
    @Mapping(target = "displayName", expression = "java( ipa.display_name() )")
    @Mapping(target = "releaseType", expression = "java( ipa.release_type() )")
    @Mapping(target = "buildType", expression = "java( ipa.build_type() )")
    @Mapping(target = "devices", source = "ipa", qualifiedByName = "mapPolyglotIPAtoIpaDevices")
    @Mapping(target = "teamName", source = "ipa", qualifiedByName = "mapPolyglotIPAtoIpaTeamName")
    @Mapping(target = "expiredDate", source = "ipa", qualifiedByName = "mapPolyglotIPAtoIpaExpiredDate")
    @Mapping(target = "plistJson", source = "ipa", qualifiedByName = "infoPlistToJsonNode")
    public abstract Ipa mapPolyglotIPAtoIpa(IPA ipa, UUID id, UUID storageCredentialId, String fileName);

    @Named("mapPolyglotIPAtoIpaDevices")
    public static List<String> mapPolyglotIPAtoIpaDevices(IPA ipa) {
        List<String> res = null;
        try {
            res = Arrays.asList(ipa.devices());
        } catch (Exception ignored) {
        }
        return res;
    }

    @Named("mapPolyglotIPAtoIpaTeamName")
    public static String mapPolyglotIPAtoIpaTeamName(IPA ipa) {
        String res = null;
        try {
            res = ipa.team_name();
        } catch (Exception ignored) {
        }
        return res;
    }

    @Named("mapPolyglotIPAtoIpaExpiredDate")
    public static Instant mapPolyglotIPAtoIpaExpiredDate(IPA ipa) {
        Instant res = null;
        try {
            res = ipa.expired_date().toInstant();
        } catch (Exception ignored) {
        }
        return res;
    }

    // map the base class AppBinary fields first before the Apk-specific ones
    @Mapping(target = "id", source = "id")
    @Mapping(target = "version", expression = "java( apk.release_version() )")
    @Mapping(target = "build", expression = "java( apk.build_version() )")
    @Mapping(target = "uploadDate", expression = "java( java.time.Instant.now() )")
    @Mapping(target = "name", expression = "java( apk.name() )")
    @Mapping(target = "identifier", expression = "java( apk.identifier() )")
    @Mapping(target = "sizeBytes", expression = "java( java.math.BigDecimal.valueOf(apk.size()) )")
    @Mapping(target = "fileName", source = "fileName")

    @Mapping(target = "storageCredential", source = "storageCredentialId")

    // then do Apk-specific fields
    @Mapping(target = "minSdkVersion", expression = "java( String.valueOf(apk.min_sdk_version()) )")
    @Mapping(target = "minOsVersion", expression = "java( String.valueOf(apk.min_os_version()) )")
    @Mapping(target = "targetSdkVersion", expression = "java( String.valueOf(apk.target_sdk_version()) )")
    @Mapping(target = "wear", expression = "java( apk.wear() )")
    @Mapping(target = "tv", expression = "java( apk.tv() )")
    @Mapping(target = "automotive", expression = "java( apk.automotive() )")
    @Mapping(target = "deviceType", expression = "java( apk.device_type() )")
    @Mapping(target = "useFeatures", expression = "java( apk.use_features() == null ? null : java.util.Arrays.asList(apk.use_features()) )")
    @Mapping(target = "usePermissions", expression = "java( apk.use_permissions() == null ? null : java.util.Arrays.asList(apk.use_permissions()) )")
    @Mapping(target = "deepLinks", expression = "java( apk.deep_links() == null ? null : java.util.Arrays.asList(apk.deep_links()) )")
    @Mapping(target = "schemes", expression = "java( apk.schemes() == null ? null : java.util.Arrays.asList(apk.schemes()) )")
    @Mapping(target = "manifestXml", expression = "java( apk.manifest().to_xml(4L) )")
    public abstract Apk mapPolyglotAPKtoApk(APK apk, UUID id, UUID storageCredentialId, String fileName);

    @Mapping(target = "subject", expression = "java( cert.certificate().getMember(\"subject\").execute().getMember(\"to_s\").execute().asString() )")
    @Mapping(target = "issuer", expression = "java( cert.certificate().getMember(\"issuer\").execute().getMember(\"to_s\").execute().asString() )")
    @Mapping(target = "notBefore", expression = "java( cert.certificate().getMember(\"not_before\").execute().asInstant() )")
    @Mapping(target = "notAfter", expression = "java( cert.certificate().getMember(\"not_after\").execute().asInstant() )")
    @Mapping(target = "path", expression = "java( cert.path() )")
    public abstract ApkCert mapPolyglotAPKCertificateToApkCert(APK.Certificate cert);
}
