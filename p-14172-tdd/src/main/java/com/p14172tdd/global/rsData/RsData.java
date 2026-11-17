package com.p14172tdd.global.rsData;

// 모든 API 응답 모양: 결과 코드, 메시지, 데이터
public record RsData<T>(String resultCode, String msg, T data) {
}
