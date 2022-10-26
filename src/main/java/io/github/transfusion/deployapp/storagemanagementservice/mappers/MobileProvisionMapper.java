package io.github.transfusion.deployapp.storagemanagementservice.mappers;

import io.github.transfusion.app_info_java_graalvm.AppInfo.MobileProvision;
import io.github.transfusion.deployapp.dto.response.IpaMobileprovisionDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.IpaMobileprovision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(componentModel = "spring")
public abstract class MobileProvisionMapper {
    Logger logger = LoggerFactory.getLogger(MobileProvisionMapper.class);
//    MobileProvisionMapper instance = Mappers.getMapper(MobileProvisionMapper.class);

    @Mapping(target = "name", expression = "java(m.name())")
    @Mapping(target = "appName", expression = "java(m.app_name())")
    @Mapping(target = "type", expression = "java(m.type())")
    @Mapping(target = "platform", expression = "java(m.platform())")
    @Mapping(target = "teamName", expression = "java(m.team_name())")
    @Mapping(target = "profileName", expression = "java(m.profile_name())")
    @Mapping(target = "createdDate", expression = "java(m.created_date().toInstant())")
    @Mapping(target = "expiredDate", expression = "java(m.expired_date().toInstant())")
    @Mapping(target = "adhoc", expression = "java(m.adhoc_question())")
    @Mapping(target = "development", expression = "java(m.development_question())")
    @Mapping(target = "enterprise", expression = "java(m.enterprise_question())")
    @Mapping(target = "appstore", expression = "java(m.appstore_question())")
    @Mapping(target = "inhouse", expression = "java(m.inhouse_question())")

    @Mapping(target = "platforms", expression = "java( m.platforms() == null ? null : java.util.Arrays.asList(m.platforms()))")
    @Mapping(target = "devices", expression = "java( m.devices() == null ? null : java.util.Arrays.asList(m.devices()) )")
    @Mapping(target = "team_identifier", expression = "java( m.team_identifier() == null ? null : java.util.Arrays.asList(m.team_identifier())   )")
    @Mapping(target = "enabled_capabilities", expression = "java( m.enabled_capabilities() == null ? null : java.util.Arrays.asList(m.enabled_capabilities()))")
    public abstract IpaMobileprovision mapPolyglotMobileProvisionToIpaMobileProvision(MobileProvision m);

    public abstract IpaMobileprovisionDTO toDTO(IpaMobileprovision ipaMobileprovision);
}
