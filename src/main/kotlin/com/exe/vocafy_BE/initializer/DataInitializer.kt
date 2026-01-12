package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.Profile
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.Topic
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.model.entity.Vocabulary
import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import com.exe.vocafy_BE.model.entity.VocabularyMedia
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.ProfileRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.TopicRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.MediaType
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.ScriptType
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInitializer {

    @Bean
    fun seedLearningData(
        syllabusRepository: SyllabusRepository,
        courseRepository: CourseRepository,
        vocabularyRepository: VocabularyRepository,
        topicRepository: TopicRepository,
        userRepository: UserRepository,
        profileRepository: ProfileRepository,
        vocabularyTermRepository: VocabularyTermRepository,
        vocabularyMeaningRepository: VocabularyMeaningRepository,
        vocabularyMediaRepository: VocabularyMediaRepository,
    ) = ApplicationRunner {
        if (userRepository.count() == 0L) {
            val users = listOf(
                User(email = "admin@vocafy.local", role = Role.ADMIN, status = Status.ACTIVE),
                User(email = "manager1@vocafy.local", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "manager2@vocafy.local", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "user1@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user2@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user3@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user4@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user5@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user6@vocafy.local", role = Role.USER, status = Status.ACTIVE),
                User(email = "user7@vocafy.local", role = Role.USER, status = Status.ACTIVE),
            )
            val savedUsers = userRepository.saveAll(users)
            val profiles = savedUsers.mapIndexed { index, user ->
                Profile(
                    user = user,
                    displayName = when (index) {
                        0 -> "Admin User"
                        1 -> "Manager One"
                        2 -> "Manager Two"
                        else -> "User ${index - 2}"
                    },
                )
            }
            userRepository.flush()
            profileRepository.saveAll(profiles)
        }

        if (
            syllabusRepository.count() > 0 ||
            courseRepository.count() > 0 ||
            vocabularyRepository.count() > 0 ||
            topicRepository.count() > 0 ||
            vocabularyTermRepository.count() > 0 ||
            vocabularyMeaningRepository.count() > 0 ||
            vocabularyMediaRepository.count() > 0
        ) {
            return@ApplicationRunner
        }

        val users = userRepository.findAll()
        if (users.isEmpty()) {
            return@ApplicationRunner
        }

        val owner = users.first()
        val syllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N5 Starter",
                description = "Starter syllabus for daily conversation and travel basics.",
                totalDays = 30,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PUBLIC,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        data class VocabSeed(
            val jaKanji: String,
            val jaKana: String,
            val en: String,
            val meaning: String,
            val partOfSpeech: PartOfSpeech,
        )

        data class CourseSeed(
            val title: String,
            val description: String,
            val vocabularies: List<VocabSeed>,
        )

        data class TopicSeed(
            val title: String,
            val description: String,
            val totalDays: Int,
            val courses: List<CourseSeed>,
        )

        val topicSeeds = listOf(
            TopicSeed(
                title = "Greetings",
                description = "Basic greetings and introductions.",
                totalDays = 7,
                courses = listOf(
                    CourseSeed(
                        title = "Hello & Goodbye",
                        description = "Greetings for meeting and parting.",
                        vocabularies = listOf(
                            VocabSeed("こんにちは", "こんにちは", "hello", "hello", PartOfSpeech.INTERJ),
                            VocabSeed("さようなら", "さようなら", "goodbye", "goodbye", PartOfSpeech.INTERJ),
                            VocabSeed("おはよう", "おはよう", "good morning", "good morning", PartOfSpeech.INTERJ),
                            VocabSeed("こんばんは", "こんばんは", "good evening", "good evening", PartOfSpeech.INTERJ),
                            VocabSeed("ありがとう", "ありがとう", "thank you", "thank you", PartOfSpeech.INTERJ),
                            VocabSeed("すみません", "すみません", "excuse me", "excuse me/sorry", PartOfSpeech.INTERJ),
                            VocabSeed("はい", "はい", "yes", "yes", PartOfSpeech.INTERJ),
                            VocabSeed("いいえ", "いいえ", "no", "no", PartOfSpeech.INTERJ),
                            VocabSeed("またね", "またね", "see you", "see you later", PartOfSpeech.INTERJ),
                            VocabSeed("はじめまして", "はじめまして", "nice to meet you", "nice to meet you", PartOfSpeech.INTERJ),
                        ),
                    ),
                    CourseSeed(
                        title = "Self Introduction",
                        description = "Introduce yourself and ask names.",
                        vocabularies = listOf(
                            VocabSeed("私", "わたし", "I", "I/me", PartOfSpeech.PRON),
                            VocabSeed("名前", "なまえ", "name", "name", PartOfSpeech.NOUN),
                            VocabSeed("学生", "がくせい", "student", "student", PartOfSpeech.NOUN),
                            VocabSeed("先生", "せんせい", "teacher", "teacher", PartOfSpeech.NOUN),
                            VocabSeed("会社員", "かいしゃいん", "company employee", "company employee", PartOfSpeech.NOUN),
                            VocabSeed("出身", "しゅっしん", "hometown", "hometown/origin", PartOfSpeech.NOUN),
                            VocabSeed("日本", "にほん", "Japan", "Japan", PartOfSpeech.NOUN),
                            VocabSeed("ベトナム", "べとなむ", "Vietnam", "Vietnam", PartOfSpeech.NOUN),
                            VocabSeed("です", "です", "to be", "polite copula", PartOfSpeech.VERB),
                            VocabSeed("よろしく", "よろしく", "please", "please/pleased to meet you", PartOfSpeech.INTERJ),
                        ),
                    ),
                    CourseSeed(
                        title = "Polite Expressions",
                        description = "Polite phrases for daily use.",
                        vocabularies = listOf(
                            VocabSeed("お願いします", "おねがいします", "please", "please", PartOfSpeech.INTERJ),
                            VocabSeed("どうぞ", "どうぞ", "here you go", "please go ahead", PartOfSpeech.INTERJ),
                            VocabSeed("失礼します", "しつれいします", "excuse me", "excuse me (polite)", PartOfSpeech.INTERJ),
                            VocabSeed("大丈夫", "だいじょうぶ", "ok", "okay/alright", PartOfSpeech.ADJ),
                            VocabSeed("ちょっと", "ちょっと", "a little", "a little bit", PartOfSpeech.ADV),
                            VocabSeed("今", "いま", "now", "now", PartOfSpeech.NOUN),
                            VocabSeed("後で", "あとで", "later", "later", PartOfSpeech.ADV),
                            VocabSeed("早く", "はやく", "quickly", "quickly", PartOfSpeech.ADV),
                            VocabSeed("ゆっくり", "ゆっくり", "slowly", "slowly", PartOfSpeech.ADV),
                            VocabSeed("少し", "すこし", "a little", "a little", PartOfSpeech.ADV),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Daily Life",
                description = "Common daily activities and routines.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "Morning Routine",
                        description = "Wake up and start the day.",
                        vocabularies = listOf(
                            VocabSeed("起きる", "おきる", "wake up", "wake up", PartOfSpeech.VERB),
                            VocabSeed("朝", "あさ", "morning", "morning", PartOfSpeech.NOUN),
                            VocabSeed("顔", "かお", "face", "face", PartOfSpeech.NOUN),
                            VocabSeed("洗う", "あらう", "wash", "wash", PartOfSpeech.VERB),
                            VocabSeed("朝ご飯", "あさごはん", "breakfast", "breakfast", PartOfSpeech.NOUN),
                            VocabSeed("コーヒー", "こーひー", "coffee", "coffee", PartOfSpeech.NOUN),
                            VocabSeed("水", "みず", "water", "water", PartOfSpeech.NOUN),
                            VocabSeed("新聞", "しんぶん", "newspaper", "newspaper", PartOfSpeech.NOUN),
                            VocabSeed("読む", "よむ", "read", "read", PartOfSpeech.VERB),
                            VocabSeed("出かける", "でかける", "go out", "go out", PartOfSpeech.VERB),
                        ),
                    ),
                    CourseSeed(
                        title = "At Work",
                        description = "Workplace basics.",
                        vocabularies = listOf(
                            VocabSeed("仕事", "しごと", "work", "work/job", PartOfSpeech.NOUN),
                            VocabSeed("会議", "かいぎ", "meeting", "meeting", PartOfSpeech.NOUN),
                            VocabSeed("資料", "しりょう", "document", "materials", PartOfSpeech.NOUN),
                            VocabSeed("メール", "めーる", "email", "email", PartOfSpeech.NOUN),
                            VocabSeed("送る", "おくる", "send", "send", PartOfSpeech.VERB),
                            VocabSeed("電話", "でんわ", "phone", "telephone", PartOfSpeech.NOUN),
                            VocabSeed("話す", "はなす", "speak", "talk", PartOfSpeech.VERB),
                            VocabSeed("休む", "やすむ", "rest", "rest", PartOfSpeech.VERB),
                            VocabSeed("昼ご飯", "ひるごはん", "lunch", "lunch", PartOfSpeech.NOUN),
                            VocabSeed("忙しい", "いそがしい", "busy", "busy", PartOfSpeech.ADJ),
                        ),
                    ),
                    CourseSeed(
                        title = "Evening Routine",
                        description = "End of the day routines.",
                        vocabularies = listOf(
                            VocabSeed("帰る", "かえる", "return home", "go back", PartOfSpeech.VERB),
                            VocabSeed("晩ご飯", "ばんごはん", "dinner", "dinner", PartOfSpeech.NOUN),
                            VocabSeed("料理", "りょうり", "cooking", "cooking", PartOfSpeech.NOUN),
                            VocabSeed("食べる", "たべる", "eat", "eat", PartOfSpeech.VERB),
                            VocabSeed("風呂", "ふろ", "bath", "bath", PartOfSpeech.NOUN),
                            VocabSeed("入る", "はいる", "enter", "enter", PartOfSpeech.VERB),
                            VocabSeed("テレビ", "てれび", "TV", "television", PartOfSpeech.NOUN),
                            VocabSeed("見る", "みる", "watch", "watch", PartOfSpeech.VERB),
                            VocabSeed("寝る", "ねる", "sleep", "sleep", PartOfSpeech.VERB),
                            VocabSeed("夜", "よる", "night", "night", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Travel",
                description = "Useful phrases for traveling.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "At the Airport",
                        description = "Airport vocabulary and phrases.",
                        vocabularies = listOf(
                            VocabSeed("空港", "くうこう", "airport", "airport", PartOfSpeech.NOUN),
                            VocabSeed("飛行機", "ひこうき", "airplane", "airplane", PartOfSpeech.NOUN),
                            VocabSeed("切符", "きっぷ", "ticket", "ticket", PartOfSpeech.NOUN),
                            VocabSeed("荷物", "にもつ", "luggage", "luggage", PartOfSpeech.NOUN),
                            VocabSeed("パスポート", "ぱすぽーと", "passport", "passport", PartOfSpeech.NOUN),
                            VocabSeed("出発", "しゅっぱつ", "departure", "departure", PartOfSpeech.NOUN),
                            VocabSeed("到着", "とうちゃく", "arrival", "arrival", PartOfSpeech.NOUN),
                            VocabSeed("搭乗口", "とうじょうぐち", "gate", "boarding gate", PartOfSpeech.NOUN),
                            VocabSeed("待つ", "まつ", "wait", "wait", PartOfSpeech.VERB),
                            VocabSeed("時間", "じかん", "time", "time", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "In the City",
                        description = "Moving around the city.",
                        vocabularies = listOf(
                            VocabSeed("駅", "えき", "station", "station", PartOfSpeech.NOUN),
                            VocabSeed("電車", "でんしゃ", "train", "train", PartOfSpeech.NOUN),
                            VocabSeed("バス", "ばす", "bus", "bus", PartOfSpeech.NOUN),
                            VocabSeed("地図", "ちず", "map", "map", PartOfSpeech.NOUN),
                            VocabSeed("道", "みち", "road", "road", PartOfSpeech.NOUN),
                            VocabSeed("右", "みぎ", "right", "right", PartOfSpeech.NOUN),
                            VocabSeed("左", "ひだり", "left", "left", PartOfSpeech.NOUN),
                            VocabSeed("近い", "ちかい", "near", "near", PartOfSpeech.ADJ),
                            VocabSeed("遠い", "とおい", "far", "far", PartOfSpeech.ADJ),
                            VocabSeed("行く", "いく", "go", "go", PartOfSpeech.VERB),
                        ),
                    ),
                    CourseSeed(
                        title = "At the Restaurant",
                        description = "Ordering and dining.",
                        vocabularies = listOf(
                            VocabSeed("レストラン", "れすとらん", "restaurant", "restaurant", PartOfSpeech.NOUN),
                            VocabSeed("メニュー", "めにゅー", "menu", "menu", PartOfSpeech.NOUN),
                            VocabSeed("注文", "ちゅうもん", "order", "order", PartOfSpeech.NOUN),
                            VocabSeed("水", "みず", "water", "water", PartOfSpeech.NOUN),
                            VocabSeed("肉", "にく", "meat", "meat", PartOfSpeech.NOUN),
                            VocabSeed("魚", "さかな", "fish", "fish", PartOfSpeech.NOUN),
                            VocabSeed("野菜", "やさい", "vegetable", "vegetable", PartOfSpeech.NOUN),
                            VocabSeed("美味しい", "おいしい", "delicious", "delicious", PartOfSpeech.ADJ),
                            VocabSeed("辛い", "からい", "spicy", "spicy", PartOfSpeech.ADJ),
                            VocabSeed("払う", "はらう", "pay", "pay", PartOfSpeech.VERB),
                        ),
                    ),
                ),
            ),
        )

        val topics = topicSeeds.mapIndexed { index, seed ->
            Topic(
                syllabus = syllabus,
                title = seed.title,
                description = seed.description,
                totalDays = seed.totalDays,
                sortOrder = index + 1,
            )
        }
        val savedTopics = topicRepository.saveAll(topics)

        val courses = mutableListOf<Course>()
        savedTopics.zip(topicSeeds).forEach { (topic, seed) ->
            seed.courses.forEachIndexed { index, courseSeed ->
                courses.add(
                    Course(
                        title = courseSeed.title,
                        description = courseSeed.description,
                        sortOrder = index + 1,
                        syllabusTopic = topic,
                    )
                )
            }
        }
        val savedCourses = courseRepository.saveAll(courses)

        val vocabularies = mutableListOf<Vocabulary>()
        val vocabSeeds = mutableListOf<Pair<VocabSeed, Vocabulary>>()
        var courseOffset = 0
        savedTopics.zip(topicSeeds).forEach { (_, topicSeed) ->
            topicSeed.courses.forEach { courseSeed ->
                val course = savedCourses[courseOffset]
                courseOffset += 1
                courseSeed.vocabularies.forEachIndexed { index, vocabSeed ->
                    val vocab = Vocabulary(
                        course = course,
                        note = null,
                        sortOrder = index + 1,
                    )
                    vocabularies.add(vocab)
                    vocabSeeds.add(vocabSeed to vocab)
                }
            }
        }
        val savedVocabularies = vocabularyRepository.saveAll(vocabularies)

        val terms = mutableListOf<VocabularyTerm>()
        val meanings = mutableListOf<VocabularyMeaning>()
        val medias = mutableListOf<VocabularyMedia>()
        savedVocabularies.zip(vocabSeeds.map { it.first }).forEach { (vocab, seed) ->
            terms.add(
                VocabularyTerm(
                    vocabulary = vocab,
                    languageCode = LanguageCode.JA,
                    scriptType = ScriptType.KANJI,
                    textValue = seed.jaKanji,
                )
            )
            terms.add(
                VocabularyTerm(
                    vocabulary = vocab,
                    languageCode = LanguageCode.JA,
                    scriptType = ScriptType.KANA,
                    textValue = seed.jaKana,
                )
            )
            terms.add(
                VocabularyTerm(
                    vocabulary = vocab,
                    languageCode = LanguageCode.EN,
                    scriptType = ScriptType.LATIN,
                    textValue = seed.en,
                )
            )
            meanings.add(
                VocabularyMeaning(
                    vocabulary = vocab,
                    languageCode = LanguageCode.EN,
                    meaningText = seed.meaning,
                    partOfSpeech = seed.partOfSpeech,
                    senseOrder = 1,
                )
            )
            medias.add(
                VocabularyMedia(
                    vocabulary = vocab,
                    mediaType = MediaType.IMAGE,
                    url = "https://example.com/media/${vocab.id ?: 0}.png",
                )
            )
        }

        vocabularyTermRepository.saveAll(terms)
        vocabularyMeaningRepository.saveAll(meanings)
        vocabularyMediaRepository.saveAll(medias)
    }
}
