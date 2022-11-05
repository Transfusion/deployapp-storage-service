package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.transfusion.app_info_java_graalvm.AppInfo.IPA;
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
    @Mapping(target = "devices", expression = "java( ipa.devices() == null ? null : java.util.Arrays.asList(ipa.devices()) )")
    @Mapping(target = "teamName", expression = "java( ipa.team_name() )")
    @Mapping(target = "expiredDate", expression = "java( ipa.expired_date().toInstant() )")
    @Mapping(target = "plistJson", source = "ipa", qualifiedByName = "infoPlistToJsonNode")
    public abstract Ipa mapPolyglotIPAtoIpa(IPA ipa, UUID id, UUID storageCredentialId, String fileName);
}
