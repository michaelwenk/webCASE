package org.openscience.webcase.elucidation.controller;

import org.openscience.webcase.elucidation.model.DataSet;
import org.openscience.webcase.elucidation.model.exchange.Transfer;
import org.openscience.webcase.elucidation.model.nmrdisplayer.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/")
public class ElucidationController {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String pathToPyLSDExecutableFolder = "/Users/mwenk/work/software/PyLSD-a4/Variant/";
    private final String pathToLSDFilterList = "/Users/mwenk/work/software/PyLSD-a4/LSD/Filters/list.txt";
    private final String pathToPyLSDInputFileFolder = "/Users/mwenk/Downloads/temp_webCASE/";
//    private final String pathToPyLSDOutputFileFolder = "/Users/mwenk/Downloads/temp_webCASE/";
    private final String pathToPyLSDLogAndErrorFolder = "/Users/mwenk/Downloads/temp_webCASE/";

    @PostMapping(value = "elucidation")
    public ResponseEntity<Transfer> elucidate(@RequestBody final Transfer requestTransfer, @RequestParam final boolean allowHeteroHeteroBonds, @RequestParam final String requestID){
        final List<DataSet> dataSetList = new ArrayList<>();
        final Data data = requestTransfer.getData();
        final String pathToPyLSDInputFile = pathToPyLSDInputFileFolder + "webcase_" + requestID + ".pylsd";

        WebClient webClient = webClientBuilder.
                baseUrl("http://localhost:8081/webcase-pylsd-create-input-file")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        uriComponentsBuilder.path("/createPyLSDInputFile")
                .queryParam("allowHeteroHeteroBonds", allowHeteroHeteroBonds)
                .queryParam("pathToPyLSDInputFile", pathToPyLSDInputFile)
                .queryParam("pathToLSDFilterList", pathToLSDFilterList)
                .queryParam("requestID", requestID);

        // create PyLSD input file
        Transfer queryTransfer = new Transfer();
        queryTransfer.setData(data);
        Transfer queryResultTransfer = webClient
                .post()
                .uri(uriComponentsBuilder.toUriString())
                .bodyValue(queryTransfer)
                .retrieve()
                .bodyToMono(Transfer.class).block();

        // run PyLSD
        if(queryResultTransfer.getPyLSDInputFileCreationWasSuccessful()){
            System.out.println("--> has been written successfully: " + pathToPyLSDInputFile);
            webClient = webClientBuilder.
                    baseUrl("http://localhost:8081/webcase-pylsd-run")
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            uriComponentsBuilder = UriComponentsBuilder.newInstance();
            uriComponentsBuilder.path("/runPyLSD")
                    .queryParam("pathToPyLSDExecutableFolder", pathToPyLSDExecutableFolder)
                    .queryParam("pathToPyLSDLogAndErrorFolder", pathToPyLSDLogAndErrorFolder)
                    .queryParam("pathToPyLSDInputFile", pathToPyLSDInputFile)
                    .queryParam("requestID", requestID);

            // create PyLSD input file
            queryTransfer = new Transfer();
            queryResultTransfer = webClient
                    .get()
                    .uri(uriComponentsBuilder.toUriString())
                    .retrieve()
                    .bodyToMono(Transfer.class).block();
            System.out.println("--> has been executed successfully: " + queryResultTransfer.getPyLSDRunWasSuccessful());
        }


        final Transfer resultTransfer = new Transfer();
        resultTransfer.setDataSetList(dataSetList);

        return new ResponseEntity<>(resultTransfer, HttpStatus.OK);
    }
}