package br.edu.ulbra.election.candidate.service;

import br.edu.ulbra.election.candidate.client.ElectionClientService;
import br.edu.ulbra.election.candidate.client.PartyClientService;
import br.edu.ulbra.election.candidate.client.VoteClientService;
import br.edu.ulbra.election.candidate.exception.GenericOutputException;
import br.edu.ulbra.election.candidate.input.v1.CandidateInput;
import br.edu.ulbra.election.candidate.model.Candidate;
import br.edu.ulbra.election.candidate.output.v1.CandidateOutput;
import br.edu.ulbra.election.candidate.output.v1.ElectionOutput;
import br.edu.ulbra.election.candidate.output.v1.GenericOutput;
import br.edu.ulbra.election.candidate.output.v1.PartyOutput;
import br.edu.ulbra.election.candidate.repository.CandidateRepository;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionClientService electionClientService;
    private final PartyClientService partyClientService;
    private final VoteClientService voteClientService;

    private final ModelMapper modelMapper;

    private static final String MESSAGE_INVALID_ID = "Invalid id";
    private static final String MESSAGE_INVALID_CANDIDATE_NUMBER = "Invalid candidate Number";
    private static final String MESSAGE_INVALID_ELECTION_ID = "Invalid Election Id";
    private static final String MESSAGE_INVALID_PARTY_ID = "Invalid Party Id";
    private static final String MESSAGE_CANDIDATE_NOT_FOUND = "Candidate not found";

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, ModelMapper modelMapper, ElectionClientService electionClientService, PartyClientService partyClientService, VoteClientService voteClientService){
        this.candidateRepository = candidateRepository;
        this.modelMapper = modelMapper;
        this.electionClientService = electionClientService;
        this.partyClientService = partyClientService;
        this.voteClientService = voteClientService;
    }

    public List<CandidateOutput> getAll(){
        try {
            List<Candidate> candidateList = (List<Candidate>) candidateRepository.findAll();
            return candidateList.stream().map(this::toCandidateOutput).collect(Collectors.toList());
        } catch (Exception e) {
            throw new GenericOutputException(MESSAGE_CANDIDATE_NOT_FOUND);
        }
    }

    public List<CandidateOutput> getAllByPartyId(Long partyId){
        try {
            List<Candidate> candidateList = (List<Candidate>) candidateRepository.findAllByPartyId(partyId);
            return candidateList.stream().map(this::toCandidateOutput).collect(Collectors.toList());
        } catch (Exception e) {
            throw new GenericOutputException(MESSAGE_INVALID_PARTY_ID);
            }
    }

    public List<Long> findFirstByPartyId(Long partyId){
        try {
            List<Candidate> candidateList = (List<Candidate>) candidateRepository.findFirstByPartyId(partyId);
            return candidateList.stream().map(this::toOnlyCandidateOutput).collect(Collectors.toList());
        } catch (Exception e) {
            throw new GenericOutputException(MESSAGE_INVALID_PARTY_ID);
            }
    }

    public List<Long> findFirstByElectionId(Long electionId){
        try {
            List<Candidate> candidateList = (List<Candidate>) candidateRepository.findFirstByElectionId(electionId);
            return candidateList.stream().map(this::toOnlyCandidateOutputElectionId).collect(Collectors.toList());
        } catch (Exception e) {
            throw new GenericOutputException(MESSAGE_INVALID_ELECTION_ID);
        }
    }

    public CandidateOutput create(CandidateInput candidateInput) {
        validateInput(candidateInput);
        validateDuplicate(candidateInput, null);
        Candidate candidate = modelMapper.map(candidateInput, Candidate.class);
        candidate = candidateRepository.save(candidate);
        return toCandidateOutput(candidate);
    }

    public CandidateOutput getById(Long candidateId){
        if (candidateId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null){
            throw new GenericOutputException(MESSAGE_CANDIDATE_NOT_FOUND);
        }

        CandidateOutput candidateOutput = modelMapper.map(candidate, CandidateOutput.class);
        ElectionOutput electionOutput = electionClientService.getById(candidate.getElectionId());
        candidateOutput.setElectionOutput(electionOutput);
        PartyOutput partyOutput = partyClientService.getById(candidate.getPartyId());
        candidateOutput.setPartyOutput(partyOutput);
        return candidateOutput;

//        return modelMapper.map(candidate, CandidateOutput.class);


    }

    public CandidateOutput getNumberElection(Long candidateNumber){
        if (candidateNumber == null){
            throw new GenericOutputException(MESSAGE_INVALID_CANDIDATE_NUMBER);
        }

        Candidate candidate = candidateRepository.findFirstByNumberElection(candidateNumber);
        if (candidate == null){
            throw new GenericOutputException(MESSAGE_INVALID_CANDIDATE_NUMBER);
        }

        return modelMapper.map(candidate, CandidateOutput.class);
    } /* Adição */

    public CandidateOutput update(Long candidateId, CandidateInput candidateInput) {
        if (candidateId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }
        validateInput(candidateInput);
        validateDuplicate(candidateInput, candidateId);

        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null){
            throw new GenericOutputException(MESSAGE_CANDIDATE_NOT_FOUND);
        }

/******************************/
//Não pode ser excluído ou alterado um candidato caso a eleição já tenha votos vinculados.


        String electionExistsVote = voteClientService.getById(candidateInput.getElectionId());
        System.out.println(electionExistsVote.trim());

        if (!electionExistsVote.trim().equals("0")){
            throw new GenericOutputException("The election have votes, then is not possible to change it!");
        }

/******************************/

        candidate.setElectionId(candidateInput.getElectionId());
        candidate.setNumberElection(candidateInput.getNumberElection());
        candidate.setName(candidateInput.getName());
        candidate.setPartyId(candidateInput.getPartyId());
        candidate = candidateRepository.save(candidate);
        return modelMapper.map(candidate, CandidateOutput.class);
    }

    public GenericOutput delete(Long candidateId) {
        if (candidateId == null){
            throw new GenericOutputException(MESSAGE_INVALID_ID);
        }

        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null){
            throw new GenericOutputException(MESSAGE_CANDIDATE_NOT_FOUND);
        }

/******************************/
//Não pode ser excluído ou alterado um candidato caso a eleição já tenha votos vinculados.

        String electionExistsVote = voteClientService.getById(candidate.getElectionId());
        System.out.println(electionExistsVote.trim());

        if (!electionExistsVote.trim().equals("0")){
            throw new GenericOutputException("The election have votes, then is not possible to change it!");
        }

/******************************/

        candidateRepository.delete(candidate);

        return new GenericOutput("Candidate deleted");
    }

    private void validateDuplicate(CandidateInput candidateInput, Long candidateId){
        Candidate candidate = candidateRepository.findFirstByNumberElectionAndAndElectionId(candidateInput.getNumberElection(), candidateInput.getElectionId());
        if (candidate != null && candidate.getId() != candidateId){
            throw new GenericOutputException("Duplicate Candidate!");
        }
    }

    private void validateInput(CandidateInput candidateInput){
        if (StringUtils.isBlank(candidateInput.getName()) || candidateInput.getName().trim().length() < 5 || !candidateInput.getName().trim().contains(" ")){
            throw new GenericOutputException("Invalid name");
        }
        if (candidateInput.getNumberElection() == null){
            throw new GenericOutputException("Invalid Number Election");
        }
        if (candidateInput.getPartyId() == null){
            throw new GenericOutputException("Invalid Party");
        }

//O início do número do candidato deve ser o mesmo início do número do partido
//Candidato deve estar vinculado a um partido válido
        try{
            PartyOutput partyOutput = partyClientService.getById(candidateInput.getPartyId());
            if (!candidateInput.getNumberElection().toString().startsWith(partyOutput.getNumber().toString())){
                throw new GenericOutputException("Number doesn't belong to party");
            }
        } catch (FeignException e){
            if (e.status() == 500) {
                throw new GenericOutputException("Invalid Party");
            }
        }

        if (candidateInput.getElectionId() == null){
            throw new GenericOutputException(MESSAGE_INVALID_ELECTION_ID);
        }

//Candidato deve estar vinculado a uma eleição válida
        try {
            electionClientService.getById(candidateInput.getElectionId());
        } catch (FeignException e){
            if (e.status() == 500) {
                throw new GenericOutputException(MESSAGE_INVALID_ELECTION_ID);
            }
        }
    }

    public CandidateOutput toCandidateOutput(Candidate candidate){
        CandidateOutput candidateOutput = modelMapper.map(candidate, CandidateOutput.class);
        ElectionOutput electionOutput = electionClientService.getById(candidate.getElectionId());
        candidateOutput.setElectionOutput(electionOutput);
        PartyOutput partyOutput = partyClientService.getById(candidate.getPartyId());
        candidateOutput.setPartyOutput(partyOutput);
        return candidateOutput;
    }

    public Long toOnlyCandidateOutput(Candidate candidate){
        CandidateOutput candidateOutput = modelMapper.map(candidate, CandidateOutput.class);
        return candidateOutput.getPartyId();
    }

    public Long toOnlyCandidateOutputElectionId(Candidate candidate){
        CandidateOutput candidateOutput = modelMapper.map(candidate, CandidateOutput.class);
        return candidateOutput.getId();
    }


}
