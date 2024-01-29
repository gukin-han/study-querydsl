package study.querydsl.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() {
        final Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        final Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        final List<Member> result1 = memberJpaRepository.findAll();
        assertThat(result1).containsExactly(member);

        final List<Member> result2 = memberJpaRepository.findByUsername("member1");
        assertThat(result2).containsExactly(member);
    }

    @Test
    public void basicQuerydslTest() {
        final Member member = new Member("member1", 10);
        memberJpaRepository.save(member);

        final Member findMember = memberJpaRepository.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);

        final List<Member> result1 = memberJpaRepository.findAll_q();
        assertThat(result1).containsExactly(member);

        final List<Member> result2 = memberJpaRepository.findByUsername_q("member1");
        assertThat(result2).containsExactly(member);
    }

}