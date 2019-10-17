package pt.ulisboa.tecnico.socialsoftware.tutor.assessment.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicConjunctionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.AssessmentRepository
import spock.lang.Specification

@DataJpaTest
class CreateAssessmentServiceSpockTest extends Specification {
    public static final String ASSESSMENT_TITLE = 'assessment title'
    public static final String TOPIC_NAME = "topic name"

    @Autowired
    AssessmentService assessmentService

    @Autowired
    AssessmentRepository assessmentRepository

    def "create a assessment with one topicConjunction with one topic"() {
        given: "a assessmentDto"
        def assessmentDto = new AssessmentDto()
        assessmentDto.setTitle(ASSESSMENT_TITLE)
        assessmentDto.setStatus(Assessment.Status.AVAILABLE.name())
        def topicConjunction = new TopicConjunctionDto()
        def topic = new TopicDto()
        topic.setName(TOPIC_NAME)
        def topicList = new ArrayList()
        topicList.add(topic)
        topicConjunction.setTopics(topicList)
        def topicConjunctionList = new ArrayList()
        topicConjunctionList.add(topicConjunction)
        assessmentDto.setTopicConjunctions(topicConjunctionList)

        when:
        assessmentService.createAssessment(assessmentDto)

        then: "the correct assessment is inside the repository"
        assessmentRepository.count() == 1L
        def result = assessmentRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == Assessment.Status.AVAILABLE
        result.getTitle() == ASSESSMENT_TITLE
        result.getTopicConjunctions().size() == 1
        def resTopicConjunction = result.getTopicConjunctions().first()
        resTopicConjunction.getId() != null
        resTopicConjunction.getTopics().size() == 1
        def resTopic = resTopicConjunction.getTopics().first()
        resTopic.getId() != null
        resTopic.getName() == TOPIC_NAME
    }

    @TestConfiguration
    static class AssessmentServiceImplTestContextConfiguration {

        @Bean
        AssessmentService assessmentService() {
            return new AssessmentService()
        }
    }

}
