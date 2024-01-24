package study.querydsl;


import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach()
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        final Team teamA = new Team("teamA");
        final Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        final Member member1 = new Member("member1", 10, teamA);
        final Member member2 = new Member("member2", 20, teamA);
        final Member member3 = new Member("member3", 30, teamB);
        final Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL(){
        // member1을 찾아라
        final String qlString = "select m from Member m where m.username =:username";

        final Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
//        final QMember m = new QMember("m"); // 별칭이다.
//        final QMember member = QMember.member;
        final QMember m1 = new QMember("m1"); // 같은 테이블을 셀프 조인하는 경우 이런식으로 선언해서 사용

        final Member findMember = queryFactory
                .select(m1)
                .from(m1)
                .where(m1.username.eq("member1")) // 파라미터 바인딩을 따로 안해준다
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    @Test
    public void search() throws Exception{
        System.out.println("search 시작");
        final Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.between(10, 30)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() throws Exception{
        System.out.println("search 시작");
        final Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1"),
                        member.age.between(10, 30))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resutlFetch() {
//        final List<Member> fetch = queryFactory
//                .selectFrom(member)
//                .fetch();
//
//        final Member fetchOne = queryFactory.selectFrom(member).fetchOne();
//
//        final Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();

//        final QueryResults<Member> results = queryFactory.selectFrom(member)
//                .fetchResults();
//
//        results.getTotal();
//        final List<Member> results1 = results.getResults();

        final long total = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        final List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        final Member member5 = result.get(0);
        final Member member6 = result.get(1);
        final Member memberNull = result.get(2);
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    @Test
    public void paging1() {
        final List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();


        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() {
        final QueryResults<Member> qr = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();


        assertThat(qr.getTotal()).isEqualTo(4);
        assertThat(qr.getLimit()).isEqualTo(2);
        assertThat(qr.getOffset()).isEqualTo(1);
        assertThat(qr.getResults().size()).isEqualTo(2);
    }
}
