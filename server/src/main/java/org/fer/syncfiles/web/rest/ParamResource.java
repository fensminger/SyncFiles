package org.fer.syncfiles.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ParamList;
import org.fer.syncfiles.service.ParamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * REST controller for managing Param.
 */
@RestController
@RequestMapping("/api")
public class ParamResource {

    private final Logger log = LoggerFactory.getLogger(ParamResource.class);

    @Inject
    private ParamService paramService;

    /**
     * POST  /params -> Create a new param.
     */
    @RequestMapping(value = "/params",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Param param) throws URISyntaxException {
        log.debug("REST request to save Param : {}", param);
        if (param.getKey() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new param cannot already have an ID").build();
        }
//        paramRepository.save(param);
        paramService.save(param);
        return ResponseEntity.created(new URI("/api/params/" + param.getKey())).build();
    }

    /**
     * PUT  /params -> Updates an existing param.
     */
    @RequestMapping(value = "/params",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Param param) throws URISyntaxException {
        log.debug("REST request to update Param : {}", param);
        if (param.getKey() == null) {
            return create(param);
        }
        paramService.save(param);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /params -> get all the params.
     */
    @RequestMapping(value = "/params",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<org.fer.syncfiles.model.Param> getAll() {
        log.debug("REST request to get all Params");
        ParamList paramList = paramService.findAll();
//        List<Param> res = paramList.getParams().stream()
//            .map(p -> {
//                    Param param = new Param();
//                    param.setId(p.getKey());
//                    param.setName(p.getName());
//                    param.setIncludeDir(p.isIncludeDir());
//                    param.setMasterDir(p.getMasterDir());
//                    param.setSlaveDir(p.getSlaveDir());
//                    return param;
//                })
//            .collect(Collectors.toList());

        return paramList.getParams();
        //return paramRepository.findAll();
    }

    /**
     * GET  /params/:id -> get the "id" param.
     */
    @RequestMapping(value = "/params/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Param get(@PathVariable String id) {
        log.debug("REST request to get Param : {}", id);
//        return Optional.ofNullable(paramRepository.findOne(id))
//            .map(param -> new ResponseEntity<>(
//                param,
//                HttpStatus.OK))
//            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        return paramService.load(id);
    }

    /**
     * DELETE  /params/:id -> delete the "id" param.
     */
    @RequestMapping(value = "/params/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable String id) {
        log.debug("REST request to delete Param : {}", id);
        paramService.delete(id);
//        paramRepository.delete(id);
    }
}
