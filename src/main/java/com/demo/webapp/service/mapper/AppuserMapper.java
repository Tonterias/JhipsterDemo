package com.demo.webapp.service.mapper;

import com.demo.webapp.domain.Appuser;
import com.demo.webapp.domain.User;
import com.demo.webapp.service.dto.AppuserDTO;
import com.demo.webapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Appuser} and its DTO {@link AppuserDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppuserMapper extends EntityMapper<AppuserDTO, Appuser> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    AppuserDTO toDto(Appuser s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
