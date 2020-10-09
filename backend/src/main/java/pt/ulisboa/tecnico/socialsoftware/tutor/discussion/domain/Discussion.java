package pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Entity
@Table(name = "discussions")
public class Discussion implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(optional=false)
    @JoinColumn(name="questionAnswer_id")
    private QuestionAnswer questionAnswer;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    private User user;

    @NotNull
    @Column(name="message", columnDefinition="text")
    private String message;

    @NotNull
    @Column(name="user_id")
    private Integer userId;

    @NotNull
    @Column(name="question_id")
    private Integer questionId;

    @NotNull
    @Column(name="course_execution_id")
    private Integer courseExecutionId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "discussion", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    private LocalDateTime date;

    @Column(columnDefinition = "boolean default false")
    private boolean available;

    @Column(columnDefinition = "boolean default false")
    private boolean closed;

    public Integer getId() {
        return id;
    }

    public Discussion(){}

    public Discussion(User user, QuestionAnswer questionAnswer, DiscussionDto discussionDto) {
        checkConsistentDiscussion(discussionDto);
        this.user = user;
        this.userId = user.getId();
        this.user.addDiscussion(this);
        this.message = discussionDto.getMessage();
        this.setUserId(user.getId());
        this.date = DateHandler.toLocalDateTime(discussionDto.getDate());
        this.available = discussionDto.isAvailable();
        this.setQuestionId(questionAnswer.getQuizQuestion().getQuestion().getId());
        this.courseExecutionId = discussionDto.getCourseExecutionId();
        this.closed = discussionDto.isClosed();
        this.questionAnswer = questionAnswer;
        this.questionAnswer.setDiscussion(this);
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public void addReply(Reply reply){
        this.replies.add(reply);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    private void checkConsistentDiscussion(DiscussionDto discussionDto) {
        if (discussionDto.getMessage() == null || discussionDto.getMessage().trim().length() == 0){
            throw new TutorException(DISCUSSION_MISSING_MESSAGE);
        }
        if (discussionDto.getDate() == null || discussionDto.getDate().trim().length() == 0){
            throw new TutorException(DISCUSSION_DATE_MISSING);
        }
    }

    public void changeStatus() {
        this.closed = !this.closed;
    }

    public boolean teacherAnswered() {
        return this.getReplies().stream().anyMatch(reply -> reply.getUser().isTeacher());
    }

    public void remove() {
        if(questionAnswer != null){
            questionAnswer.getDiscussion().remove();
            questionAnswer = null;
        }

        user.getDiscussions().remove(this);
        user = null;

        replies.clear();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDiscussion(this);
    }

    public QuestionAnswer getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public Integer getCourseExecutionId() {
        return courseExecutionId;
    }

    public void setCourseExecutionId(Integer courseExecutionId) {
        this.courseExecutionId = courseExecutionId;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean hasPublicReplies() {
        return this.replies.stream().anyMatch(Reply::isAvailable);
    }
}
