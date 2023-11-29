package smu.mp.project.todo;

// 각 할 일 항목의 데이터 담고 있는 클래스
public class TodoItem {
    private String content;  // 할 일의 내용

    private String memo;  // 할 일에 대한 메모

    private String startTime; // 할 일에 설정된 시간
    private String endTime;

    private String date; // 할 일 날짜

    // 생성자
    public TodoItem(String content, String memo, String startTime, String endTime, String date) {
        this.content = content;
        this.memo = memo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }

    // 할 일의 내용을 반환하는 getter 메소드
    public String getContent() { return content;}

    // 할 일의 메모를 반환하는 getter 메소드
    public String getMemo() { return memo;}

    // 할 일의 시간을 반환하는 getter 메소드
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    public String getDate() {return date; }
}
