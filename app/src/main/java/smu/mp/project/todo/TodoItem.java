package smu.mp.project.todo;

// 할 일 항목을 나타내는 클래스
public class TodoItem {
    private String content;   // 할 일 내용
    private String memo;      // 할 일에 대한 메모
    private String startTime; // 시작 시간
    private String endTime;   // 종료 시간
    private String date;      // 할 일 날짜

    // 할 일 항목 생성자
    public TodoItem(String content, String memo, String startTime, String endTime, String date) {
        this.content = content;
        this.memo = memo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
    }

    // Getter 메소드
    public String getContent() { return content; }
    public String getMemo() { return memo; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    // Setter 메소드
    public void setContent(String content) { this.content = content; }
    public void setMemo(String memo) { this.memo = memo; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
