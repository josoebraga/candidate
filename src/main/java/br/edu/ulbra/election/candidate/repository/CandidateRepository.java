package br.edu.ulbra.election.candidate.repository;

import br.edu.ulbra.election.candidate.model.Candidate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CandidateRepository extends CrudRepository<Candidate, Long> {
    Candidate findFirstByNumberElectionAndAndElectionId(Long numberElection, Long electionId);
    Candidate findFirstByNumberElection(Long numberElection);
    List<Candidate> findAllByPartyId(Long partyId);
    List<Candidate> findFirstByPartyId(Long partyId);
    List<Candidate> findFirstByElectionId(Long electionId);
    Candidate findByPartyId(Long partyId);
}
