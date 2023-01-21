package io.github.transfusion.deployapp.storagemanagementservice.controller;

import io.github.transfusion.deployapp.dto.response.AppBinaryAliasDTO;
import io.github.transfusion.deployapp.storagemanagementservice.db.entities.AppBinaryAlias;
import io.github.transfusion.deployapp.storagemanagementservice.db.repositories.AppBinaryAliasRepository;
import io.github.transfusion.deployapp.storagemanagementservice.mappers.AppBinaryAliasMapper;
import io.github.transfusion.deployapp.storagemanagementservice.services.AliasService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/app")
public class AppBinaryAliasController {
    @Autowired
    private AliasService aliasService;

    @Autowired
    private AppBinaryAliasMapper appBinaryAliasMapper;
    @Autowired
    private AppBinaryAliasRepository appBinaryAliasRepository;

    @PostMapping("/binary/{id}/shorten")
    @PreAuthorize("hasPermission(#id, 'APPBINARY_EDIT')")
    @Operation(summary = "Generates a short alias for the given app binary")
    public ResponseEntity<AppBinaryAliasDTO> shorten(@PathVariable("id") UUID id) {
        AppBinaryAlias alias = aliasService.generateAlias(id);
        return new ResponseEntity<>(appBinaryAliasMapper.toDTO(alias), HttpStatus.OK);
    }

    @GetMapping("/binary/{id}/alias")
    @PreAuthorize("hasPermission(#id, 'APPBINARY_EDIT')")
    public ResponseEntity<List<AppBinaryAliasDTO>> getAppBinaryAliases(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(appBinaryAliasRepository.findByAppBinaryId(id)
                .stream().map(appBinaryAliasMapper::toDTO)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @DeleteMapping("/binary/{id}/alias/{alias}")
    @PreAuthorize("hasPermission(#id, 'APPBINARY_EDIT')")
    public ResponseEntity<Void> deleteAppBinaryAlias(@PathVariable("id") UUID id, @PathVariable("alias") String alias) {
        boolean deleted = aliasService.deleteAppBinaryAlias(id, alias);
        return new ResponseEntity<>(deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/alias/{alias}")
    public ResponseEntity<AppBinaryAliasDTO> getAppBinaryAlias(@PathVariable("alias") String alias) {
        Optional<AppBinaryAlias> appBinaryAlias = aliasService.getAlias(alias);
        return appBinaryAlias
                .map(binaryAlias -> new ResponseEntity<>(appBinaryAliasMapper.toDTO(binaryAlias), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
