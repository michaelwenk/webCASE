package org.openscience.webcase.casekit.controller;

import casekit.nmr.model.DataSet;
import casekit.nmr.utils.Match;
import org.openscience.webcase.casekit.model.exchange.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/ranking")
public class RankingController {

    @PostMapping(value = "rankBySpectralSimilarity", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Transfer> rankBySpectralSimilarity(@RequestBody final Transfer queryTransfer) {
        List<DataSet> rankedDataSetList = queryTransfer.getDataSetList();
        if (queryTransfer.getQuerySpectrum()
                         .getNDim()
                == 1
                && queryTransfer.getQuerySpectrum()
                                .getNuclei()[0].equals("13C")) {
            // @TODO get shift tolerance as arguments
            rankedDataSetList = rankedDataSetList.stream()
                                                 .filter(dataSet -> Match.matchSpectra(dataSet.getSpectrum(),
                                                                                       queryTransfer.getQuerySpectrum(),
                                                                                       0, 0,
                                                                                       queryTransfer.getShiftTolerances()
                                                                                                    .get("C"))
                                                                         .isFullyAssigned(0))
                                                 .collect(Collectors.toList());
            rankedDataSetList.forEach(dataSet -> dataSet.addMetaInfo("avgDev", String.valueOf(
                    Match.calculateAverageDeviation(dataSet.getSpectrum(), queryTransfer.getQuerySpectrum(), 0, 0,
                                                    queryTransfer.getShiftTolerances()
                                                                 .get("C")))));
            rankedDataSetList.sort((dataSet1, dataSet2) -> {
                if (Double.parseDouble(dataSet1.getMeta()
                                               .get("avgDev"))
                        < Double.parseDouble(dataSet2.getMeta()
                                                     .get("avgDev"))) {
                    return -1;
                } else if (Double.parseDouble(dataSet1.getMeta()
                                                      .get("avgDev"))
                        > Double.parseDouble(dataSet2.getMeta()
                                                     .get("avgDev"))) {
                    return 1;
                }
                return 0;
            });
        }

        final Transfer resultTransfer = new Transfer();
        resultTransfer.setDataSetList(rankedDataSetList);
        return new ResponseEntity<>(resultTransfer, HttpStatus.OK);
    }

}
