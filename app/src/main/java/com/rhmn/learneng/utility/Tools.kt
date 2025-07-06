package com.rhmn.learneng.utility

import com.rhmn.learneng.data.model.*

class Tools {

    companion object {
        fun buildLessonFromDbNullable(
            vocabularies: List<Vocabulary>,
            dictations: List<Dictation>,
            pronunciations: List<Pronunciation>,
            grammarQuiz: List<Quiz>,
            finalQuiz: List<Quiz>,
            readings: List<Reading>?, // Updated to List<Reading>?
            listening: List<Listening>?, // Updated to List<Listening>?
            dialogues: List<Dialogue>
        ): Lesson {
            return Lesson(
                vocabularies = vocabularies.takeIf { it.isNotEmpty() }?.map {
                    VocabularyJson(
                        word = WordJson(it.word.sentence, it.word.translation),
                        examples = it.examples.map { ex ->
                            WordJson(ex.sentence, ex.translation)
                        }
                    )
                },
                dictations = dictations.takeIf { it.isNotEmpty() }?.map {
                    DictationJson(
                        word = WordJson(it.word.sentence, it.word.translation)
                    )
                },
                pronunciations = pronunciations.takeIf { it.isNotEmpty() }?.map {
                    PronunciationJson(
                        word = WordJson(it.word.sentence, it.word.translation)
                    )
                },
                grammarQuiz = grammarQuiz
                    .filter { it.quizType == QuizType.GRAMMAR }
                    .takeIf { it.isNotEmpty() }
                    ?.map {
                        QuizJson(
                            question = it.question,
                            answer = it.answer,
                            options = it.options
                        )
                    },
                finalQuiz = finalQuiz
                    .filter { it.quizType == QuizType.FINAL }
                    .takeIf { it.isNotEmpty() }
                    ?.map {
                        QuizJson(
                            question = it.question,
                            answer = it.answer,
                            options = it.options
                        )
                    },
                readings = readings?.takeIf { it.isNotEmpty() }?.map { reading ->
                    ReadingJson(
                        title = reading.title,
                        text = reading.text,
                        translation = reading.translation,
                        questions = grammarQuiz
                            .filter { it.id in reading.quizIds }
                            .takeIf { it.isNotEmpty() }
                            ?.map {
                                QuizJson(
                                    question = it.question,
                                    answer = it.answer,
                                    options = it.options
                                )
                            }
                    )
                },
                listening = listening?.takeIf { it.isNotEmpty() }?.map { listening ->
                    ListeningJson(
                        title = listening.title,
                        dialogues = dialogues
                            .filter { it.id in listening.dialogIds }
                            .takeIf { it.isNotEmpty() }
                            ?.map {
                                DialogueJson(
                                    speaker = it.speaker,
                                    text = it.text,
                                    translation = it.translation
                                )
                            } ?: emptyList()
                    )
                }
            )
        }
    }
}