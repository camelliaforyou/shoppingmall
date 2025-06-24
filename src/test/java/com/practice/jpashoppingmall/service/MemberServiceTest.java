package com.practice.jpashoppingmall.service;

import com.practice.jpashoppingmall.constant.Role;
import com.practice.jpashoppingmall.dto.MemberFormDto;
import com.practice.jpashoppingmall.entity.Member;
import com.practice.jpashoppingmall.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@test.com");
        memberFormDto.setName("테스트");
        memberFormDto.setAddress("서울시 강남구");
        memberFormDto.setPassword("1234test");
        memberFormDto.setRole(Role.USER);
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회원 저장 테스트")
    void saveMember(){
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getRole(), savedMember.getRole());
        assertTrue(passwordEncoder.matches("1234test", savedMember.getPassword()));
    }

    @Test
    @DisplayName("중복 회원 예외 발생 테스트")
    void saveDuplicateMember(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);
        assertThrows(IllegalStateException.class, () -> memberService.saveMember(member2));
    }

    @Test
    @DisplayName("UserDetails 조회 테스트")
    void loadUserByUsername(){
        Member member = createMember();
        memberService.saveMember(member);

        UserDetails userDetails = memberService.loadUserByUsername(member.getEmail());
        assertEquals(member.getEmail(), userDetails.getUsername());
        assertEquals(member.getPassword(), userDetails.getPassword());
    }
}
