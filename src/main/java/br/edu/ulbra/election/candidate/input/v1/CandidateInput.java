package br.edu.ulbra.election.candidate.input.v1;

import br.edu.ulbra.election.candidate.exception.GenericOutputException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Candidate Input Information")
public class CandidateInput {

    @ApiModelProperty(example = "John Doe", notes = "Candidate name")
    private String name;
    @ApiModelProperty(example = "1", notes = "Candidate Party ID")
    private Long partyId;
    @ApiModelProperty(example = "97654", notes = "Candidate Election Number")
    private Long numberElection;
    @ApiModelProperty(example = "3", notes = "Candidate Election Id")
    private Long electionId;

    public String getName() {
        if ((this.name.substring(0, (this.name.indexOf(" ") - 1))).length() >= 3 &
                (this.name.substring((this.name.indexOf(" ") + 1), this.name.length())).length() >= 3)
        {
            return name;
        } else{
            throw new GenericOutputException("Invalid name");
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPartyId() {
        return this.partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }

    public Long getNumberElection() {
        return this.numberElection;
    }

    public void setNumberElection(Long numberElection) {
        this.numberElection = numberElection;
    }

    public Long getElectionId() {
        return this.electionId;
    }

    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }
}
