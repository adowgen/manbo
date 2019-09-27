/*
*
*
* getSharedPreferences("stepCount", MODE_PRIVATE);
*
* key : reset (boolean) ------------- 자정이후 리셋, 자정때 서버 통신하면 false 로 초기화
*     : appOsCount (int) ------------ 앱과는 상관없는 step_sensor 자체의 데이터, 최초 설치시 및 자정 이후 리셋 때 걸음수를 0를 만들기 위함
*     : convertedCount (int) -------- 코인으로 전환한 워크 카운트
*     : heartCoinCount (int) -------- 하트에 클릭 가능한 코인 카운트
*     : myCash (int) ---------------- 나의 캐쉬
*
*
*
*
*
*
* getSharedPreferences("manboData", MODE_PRIVATE);
*
* key : greeting (string) ------------- 메인 인사 텍스트
*
*
*
* */