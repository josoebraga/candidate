package br.edu.ulbra.election.candidate.api.v1;

import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.output.v1.CandidateOutput;
import br.edu.ulbra.election.candidate.output.v1.GenericOutput;
import br.edu.ulbra.election.candidate.service.CandidateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/candidate")
public class CandidateApi {

    private final CandidateService candidateService;

    @Autowired
    public CandidateApi(CandidateService candidateService){
        this.candidateService = candidateService;
    }

    @GetMapping("/")
    @ApiOperation(value = "Get candidates List")
    public List<CandidateOutput> getAll(){
        return candidateService.getAll();
    }

    @GetMapping("/{candidateId}")
    @ApiOperation(value = "Get candidate by Id")
    public CandidateOutput getById(@PathVariable Long candidateId){
        return candidateService.getById(candidateId);
    }

    @GetMapping("/getByNumber/{candidateNumber}")
    @ApiOperation(value = "Get candidate by Number")
    public CandidateOutput getByCandidateNumber(@PathVariable Long candidateNumber){
        return candidateService.getNumberElection(candidateNumber);
    } /*  Adição */

    @GetMapping("/getByPartyId/{partyId}")
    @ApiOperation(value = "Get all candidate by Party Id")
    public List<CandidateOutput> getAllByPartyId(@PathVariable Long partyId){
        return candidateService.getAllByPartyId(partyId);
    } /*  Adição */

    @GetMapping("/getFirstByPartyId/{partyId}")
    @ApiOperation(value = "Get all candidate by Party Id")
    public List<Long> findFirstByPartyId(@PathVariable Long partyId){
        return candidateService.findFirstByPartyId(partyId);
    } /*  Adição */

    @PostMapping("/")
    @ApiOperation(value = "Create new candidate")
    public CandidateOutput create(@RequestBody CandidateInput candidateInput){
        return candidateService.create(candidateInput);
    }

    @PutMapping("/{candidateId}")
    @ApiOperation(value = "Update candidate")
    public CandidateOutput update(@PathVariable Long candidateId, @RequestBody CandidateInput candidateInput){
        return candidateService.update(candidateId, candidateInput);
    }

    @DeleteMapping("/{candidateId}")
    @ApiOperation(value = "Delete candidate")
    public GenericOutput delete(@PathVariable Long candidateId){
        return candidateService.delete(candidateId);
    }
}
