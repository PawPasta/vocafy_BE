package com.exe.vocafy_BE.initializer

import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.enum.SyllabusSourceType
import com.exe.vocafy_BE.enum.SyllabusVisibility
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.PaymentMethod
import com.exe.vocafy_BE.model.entity.Profile
import com.exe.vocafy_BE.model.entity.Subscription
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.Topic
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.model.entity.Vocabulary
import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import com.exe.vocafy_BE.model.entity.VocabularyMedia
import com.exe.vocafy_BE.model.entity.VocabularyQuestion
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.PaymentMethodRepository
import com.exe.vocafy_BE.repo.ProfileRepository
import com.exe.vocafy_BE.repo.SyllabusRepository
import com.exe.vocafy_BE.repo.SubscriptionRepository
import com.exe.vocafy_BE.repo.TopicRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyQuestionRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.MediaType
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.ScriptType
import com.exe.vocafy_BE.enum.VocabularyQuestionType
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
        subscriptionRepository: SubscriptionRepository,
        paymentMethodRepository: PaymentMethodRepository,
        vocabularyTermRepository: VocabularyTermRepository,
        vocabularyMeaningRepository: VocabularyMeaningRepository,
        vocabularyMediaRepository: VocabularyMediaRepository,
        vocabularyQuestionRepository: VocabularyQuestionRepository,
    ) = ApplicationRunner {
        if (userRepository.count() == 0L) {
            val users = listOf(
                User(email = "vocafy.exesp26@gmail.com", role = Role.ADMIN, status = Status.ACTIVE),
                User(email = "khiem1371@gmail.com", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "manager2@vocafy.local", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "khiem103204@gmail.com", role = Role.USER, status = Status.ACTIVE),
                User(email = "khiemngse182188@fpt.edu.vn", role = Role.USER, status = Status.ACTIVE),
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

            if (subscriptionRepository.count() == 0L) {
                val subscriptions = savedUsers.map { user ->
                    if (user.email == "khiemngse182188@fpt.edu.vn") {
                        Subscription(
                            user = user,
                            plan = SubscriptionPlan.VIP,
                            startAt = java.time.LocalDate.now(),
                            endAt = java.time.LocalDate.now().plusYears(1000),
                        )
                    } else {
                        Subscription(
                            user = user,
                            plan = SubscriptionPlan.FREE,
                        )
                    }
                }
                subscriptionRepository.saveAll(subscriptions)
            }
        }

        if (paymentMethodRepository.count() == 0L) {
            paymentMethodRepository.saveAll(
                listOf(
                    PaymentMethod(provider = "ZALOPAY", description = "ZaloPay wallet"),
                    PaymentMethod(provider = "MOMO", description = "MoMo wallet"),
                    PaymentMethod(provider = "VNPAY", description = "VNPay gateway"),
                )
            )
        }

        if (
            syllabusRepository.count() > 0 ||
            courseRepository.count() > 0 ||
            vocabularyRepository.count() > 0 ||
            topicRepository.count() > 0 ||
            vocabularyTermRepository.count() > 0 ||
            vocabularyMeaningRepository.count() > 0 ||
            vocabularyMediaRepository.count() > 0 ||
            vocabularyQuestionRepository.count() > 0
        ) {
            return@ApplicationRunner
        }

        val users = userRepository.findAll()
        if (users.isEmpty()) {
            return@ApplicationRunner
        }

        val owner = users.first()

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

        fun seedSyllabusContent(syllabus: Syllabus, topicSeeds: List<TopicSeed>) {
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
            val questions = mutableListOf<VocabularyQuestion>()
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

            val termByVocab = terms
                .filter { it.id != null && it.vocabulary.id != null }
                .groupBy { it.vocabulary.id }
                .mapValues { it.value.first() }
            val meaningByVocab = meanings
                .filter { it.id != null && it.vocabulary.id != null }
                .groupBy { it.vocabulary.id }
                .mapValues { it.value.first() }
            val mediaByVocab = medias
                .filter { it.id != null && it.vocabulary.id != null }
                .groupBy { it.vocabulary.id }
                .mapValues { it.value.first() }

            savedVocabularies.forEach { vocab ->
                val vocabId = vocab.id ?: return@forEach
                val termRef = termByVocab[vocabId]?.id
                val meaningRef = meaningByVocab[vocabId]?.id
                val mediaRef = mediaByVocab[vocabId]?.id

                if (termRef != null && meaningRef != null && mediaRef != null) {
                    questions.add(
                        VocabularyQuestion(
                            vocabulary = vocab,
                            questionType = VocabularyQuestionType.LISTEN_SELECT_TERM,
                            questionRefId = mediaRef,
                            answerRefId = termRef,
                        )
                    )
                    questions.add(
                        VocabularyQuestion(
                            vocabulary = vocab,
                            questionType = VocabularyQuestionType.LOOK_TERM_SELECT_MEANING,
                            questionRefId = termRef,
                            answerRefId = meaningRef,
                        )
                    )
                    questions.add(
                        VocabularyQuestion(
                            vocabulary = vocab,
                            questionType = VocabularyQuestionType.LOOK_MEANING_INPUT_TERM,
                            questionRefId = meaningRef,
                            answerRefId = termRef,
                        )
                    )
                    questions.add(
                        VocabularyQuestion(
                            vocabulary = vocab,
                            questionType = VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM,
                            questionRefId = mediaRef,
                            answerRefId = termRef,
                        )
                    )
                }
            }

            if (questions.isNotEmpty()) {
                vocabularyQuestionRepository.saveAll(questions)
            }
        }

        val publicSyllabus = syllabusRepository.save(
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
                    CourseSeed(
                        title = "Asking Questions",
                        description = "Simple question words and usage.",
                        vocabularies = listOf(
                            VocabSeed("何", "なに", "what", "what", PartOfSpeech.PRON),
                            VocabSeed("誰", "だれ", "who", "who", PartOfSpeech.PRON),
                            VocabSeed("どこ", "どこ", "where", "where", PartOfSpeech.PRON),
                            VocabSeed("いつ", "いつ", "when", "when", PartOfSpeech.PRON),
                            VocabSeed("なぜ", "なぜ", "why", "why", PartOfSpeech.PRON),
                            VocabSeed("どう", "どう", "how", "how", PartOfSpeech.PRON),
                            VocabSeed("どれ", "どれ", "which", "which", PartOfSpeech.PRON),
                            VocabSeed("いくら", "いくら", "how much", "how much", PartOfSpeech.PRON),
                            VocabSeed("いくつ", "いくつ", "how many", "how many", PartOfSpeech.PRON),
                            VocabSeed("どんな", "どんな", "what kind", "what kind", PartOfSpeech.PRON),
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
                    CourseSeed(
                        title = "Home Activities",
                        description = "Activities at home.",
                        vocabularies = listOf(
                            VocabSeed("家", "いえ", "house", "house/home", PartOfSpeech.NOUN),
                            VocabSeed("部屋", "へや", "room", "room", PartOfSpeech.NOUN),
                            VocabSeed("掃除", "そうじ", "cleaning", "cleaning", PartOfSpeech.NOUN),
                            VocabSeed("掃除する", "そうじする", "clean", "to clean", PartOfSpeech.VERB),
                            VocabSeed("洗濯", "せんたく", "laundry", "laundry", PartOfSpeech.NOUN),
                            VocabSeed("洗濯する", "せんたくする", "do laundry", "do laundry", PartOfSpeech.VERB),
                            VocabSeed("料理する", "りょうりする", "cook", "cook", PartOfSpeech.VERB),
                            VocabSeed("買い物", "かいもの", "shopping", "shopping", PartOfSpeech.NOUN),
                            VocabSeed("買う", "かう", "buy", "buy", PartOfSpeech.VERB),
                            VocabSeed("休む", "やすむ", "rest", "rest", PartOfSpeech.VERB),
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
                    CourseSeed(
                        title = "Shopping",
                        description = "Buying things and asking prices.",
                        vocabularies = listOf(
                            VocabSeed("店", "みせ", "shop", "shop/store", PartOfSpeech.NOUN),
                            VocabSeed("値段", "ねだん", "price", "price", PartOfSpeech.NOUN),
                            VocabSeed("高い", "たかい", "expensive", "expensive/high", PartOfSpeech.ADJ),
                            VocabSeed("安い", "やすい", "cheap", "cheap", PartOfSpeech.ADJ),
                            VocabSeed("これ", "これ", "this", "this", PartOfSpeech.PRON),
                            VocabSeed("それ", "それ", "that", "that", PartOfSpeech.PRON),
                            VocabSeed("サイズ", "さいず", "size", "size", PartOfSpeech.NOUN),
                            VocabSeed("試着", "しちゃく", "try on", "try on", PartOfSpeech.NOUN),
                            VocabSeed("買う", "かう", "buy", "buy", PartOfSpeech.VERB),
                            VocabSeed("レジ", "れじ", "checkout", "cash register", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Food & Drink",
                description = "Common foods, drinks, and flavors.",
                totalDays = 6,
                courses = listOf(
                    CourseSeed(
                        title = "Meals",
                        description = "Meals and dining items.",
                        vocabularies = listOf(
                            VocabSeed("ご飯", "ごはん", "rice/meal", "meal/rice", PartOfSpeech.NOUN),
                            VocabSeed("パン", "ぱん", "bread", "bread", PartOfSpeech.NOUN),
                            VocabSeed("牛乳", "ぎゅうにゅう", "milk", "milk", PartOfSpeech.NOUN),
                            VocabSeed("茶", "ちゃ", "tea", "tea", PartOfSpeech.NOUN),
                            VocabSeed("砂糖", "さとう", "sugar", "sugar", PartOfSpeech.NOUN),
                            VocabSeed("塩", "しお", "salt", "salt", PartOfSpeech.NOUN),
                            VocabSeed("甘い", "あまい", "sweet", "sweet", PartOfSpeech.ADJ),
                            VocabSeed("苦い", "にがい", "bitter", "bitter", PartOfSpeech.ADJ),
                            VocabSeed("熱い", "あつい", "hot", "hot", PartOfSpeech.ADJ),
                            VocabSeed("冷たい", "つめたい", "cold", "cold", PartOfSpeech.ADJ),
                        ),
                    ),
                    CourseSeed(
                        title = "Ordering Drinks",
                        description = "Drink orders and preferences.",
                        vocabularies = listOf(
                            VocabSeed("水", "みず", "water", "water", PartOfSpeech.NOUN),
                            VocabSeed("お茶", "おちゃ", "tea", "tea", PartOfSpeech.NOUN),
                            VocabSeed("コーヒー", "こーひー", "coffee", "coffee", PartOfSpeech.NOUN),
                            VocabSeed("ジュース", "じゅーす", "juice", "juice", PartOfSpeech.NOUN),
                            VocabSeed("氷", "こおり", "ice", "ice", PartOfSpeech.NOUN),
                            VocabSeed("少し", "すこし", "a little", "a little", PartOfSpeech.ADV),
                            VocabSeed("多い", "おおい", "many", "many", PartOfSpeech.ADJ),
                            VocabSeed("おかわり", "おかわり", "refill", "refill", PartOfSpeech.NOUN),
                            VocabSeed("カップ", "かっぷ", "cup", "cup", PartOfSpeech.NOUN),
                            VocabSeed("グラス", "ぐらす", "glass", "glass", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Fruits & Snacks",
                        description = "Fruits and snacks.",
                        vocabularies = listOf(
                            VocabSeed("りんご", "りんご", "apple", "apple", PartOfSpeech.NOUN),
                            VocabSeed("みかん", "みかん", "mandarin", "mandarin", PartOfSpeech.NOUN),
                            VocabSeed("バナナ", "ばなな", "banana", "banana", PartOfSpeech.NOUN),
                            VocabSeed("いちご", "いちご", "strawberry", "strawberry", PartOfSpeech.NOUN),
                            VocabSeed("お菓子", "おかし", "snack", "snack", PartOfSpeech.NOUN),
                            VocabSeed("チョコ", "ちょこ", "chocolate", "chocolate", PartOfSpeech.NOUN),
                            VocabSeed("アイス", "あいす", "ice cream", "ice cream", PartOfSpeech.NOUN),
                            VocabSeed("甘い", "あまい", "sweet", "sweet", PartOfSpeech.ADJ),
                            VocabSeed("好き", "すき", "like", "like", PartOfSpeech.ADJ),
                            VocabSeed("嫌い", "きらい", "dislike", "dislike", PartOfSpeech.ADJ),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(publicSyllabus, topicSeeds)

        val privateSyllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N5 Private Practice",
                description = "Private syllabus for guided practice.",
                totalDays = 21,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val privateTopicSeeds = listOf(
            TopicSeed(
                title = "Numbers",
                description = "Counting and basic numbers.",
                totalDays = 5,
                courses = listOf(
                    CourseSeed(
                        title = "Counting 1-10",
                        description = "Basic numbers.",
                        vocabularies = listOf(
                            VocabSeed("一", "いち", "one", "one", PartOfSpeech.NOUN),
                            VocabSeed("二", "に", "two", "two", PartOfSpeech.NOUN),
                            VocabSeed("三", "さん", "three", "three", PartOfSpeech.NOUN),
                            VocabSeed("四", "よん", "four", "four", PartOfSpeech.NOUN),
                            VocabSeed("五", "ご", "five", "five", PartOfSpeech.NOUN),
                            VocabSeed("六", "ろく", "six", "six", PartOfSpeech.NOUN),
                            VocabSeed("七", "なな", "seven", "seven", PartOfSpeech.NOUN),
                            VocabSeed("八", "はち", "eight", "eight", PartOfSpeech.NOUN),
                            VocabSeed("九", "きゅう", "nine", "nine", PartOfSpeech.NOUN),
                            VocabSeed("十", "じゅう", "ten", "ten", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Counters",
                        description = "Common counters.",
                        vocabularies = listOf(
                            VocabSeed("一つ", "ひとつ", "one (thing)", "one item", PartOfSpeech.NOUN),
                            VocabSeed("二つ", "ふたつ", "two (things)", "two items", PartOfSpeech.NOUN),
                            VocabSeed("三つ", "みっつ", "three (things)", "three items", PartOfSpeech.NOUN),
                            VocabSeed("四つ", "よっつ", "four (things)", "four items", PartOfSpeech.NOUN),
                            VocabSeed("五つ", "いつつ", "five (things)", "five items", PartOfSpeech.NOUN),
                            VocabSeed("六つ", "むっつ", "six (things)", "six items", PartOfSpeech.NOUN),
                            VocabSeed("七つ", "ななつ", "seven (things)", "seven items", PartOfSpeech.NOUN),
                            VocabSeed("八つ", "やっつ", "eight (things)", "eight items", PartOfSpeech.NOUN),
                            VocabSeed("九つ", "ここのつ", "nine (things)", "nine items", PartOfSpeech.NOUN),
                            VocabSeed("十", "とお", "ten (things)", "ten items", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Money",
                        description = "Prices and money.",
                        vocabularies = listOf(
                            VocabSeed("円", "えん", "yen", "yen", PartOfSpeech.NOUN),
                            VocabSeed("百", "ひゃく", "hundred", "hundred", PartOfSpeech.NOUN),
                            VocabSeed("千", "せん", "thousand", "thousand", PartOfSpeech.NOUN),
                            VocabSeed("万", "まん", "ten thousand", "ten thousand", PartOfSpeech.NOUN),
                            VocabSeed("安い", "やすい", "cheap", "cheap", PartOfSpeech.ADJ),
                            VocabSeed("高い", "たかい", "expensive", "expensive", PartOfSpeech.ADJ),
                            VocabSeed("買う", "かう", "buy", "buy", PartOfSpeech.VERB),
                            VocabSeed("売る", "うる", "sell", "sell", PartOfSpeech.VERB),
                            VocabSeed("お金", "おかね", "money", "money", PartOfSpeech.NOUN),
                            VocabSeed("値段", "ねだん", "price", "price", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Time",
                description = "Days, dates, and time.",
                totalDays = 7,
                courses = listOf(
                    CourseSeed(
                        title = "Days of the Week",
                        description = "Days of the week.",
                        vocabularies = listOf(
                            VocabSeed("月曜日", "げつようび", "Monday", "Monday", PartOfSpeech.NOUN),
                            VocabSeed("火曜日", "かようび", "Tuesday", "Tuesday", PartOfSpeech.NOUN),
                            VocabSeed("水曜日", "すいようび", "Wednesday", "Wednesday", PartOfSpeech.NOUN),
                            VocabSeed("木曜日", "もくようび", "Thursday", "Thursday", PartOfSpeech.NOUN),
                            VocabSeed("金曜日", "きんようび", "Friday", "Friday", PartOfSpeech.NOUN),
                            VocabSeed("土曜日", "どようび", "Saturday", "Saturday", PartOfSpeech.NOUN),
                            VocabSeed("日曜日", "にちようび", "Sunday", "Sunday", PartOfSpeech.NOUN),
                            VocabSeed("今日", "きょう", "today", "today", PartOfSpeech.NOUN),
                            VocabSeed("明日", "あした", "tomorrow", "tomorrow", PartOfSpeech.NOUN),
                            VocabSeed("昨日", "きのう", "yesterday", "yesterday", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Time of Day",
                        description = "Time expressions.",
                        vocabularies = listOf(
                            VocabSeed("時", "じ", "o'clock", "o'clock", PartOfSpeech.NOUN),
                            VocabSeed("分", "ふん", "minute", "minute", PartOfSpeech.NOUN),
                            VocabSeed("朝", "あさ", "morning", "morning", PartOfSpeech.NOUN),
                            VocabSeed("昼", "ひる", "noon", "noon", PartOfSpeech.NOUN),
                            VocabSeed("夜", "よる", "night", "night", PartOfSpeech.NOUN),
                            VocabSeed("今", "いま", "now", "now", PartOfSpeech.NOUN),
                            VocabSeed("早い", "はやい", "early", "early", PartOfSpeech.ADJ),
                            VocabSeed("遅い", "おそい", "late", "late", PartOfSpeech.ADJ),
                            VocabSeed("時間", "じかん", "time", "time", PartOfSpeech.NOUN),
                            VocabSeed("週末", "しゅうまつ", "weekend", "weekend", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Schedules",
                        description = "Schedule related terms.",
                        vocabularies = listOf(
                            VocabSeed("予定", "よてい", "schedule", "schedule", PartOfSpeech.NOUN),
                            VocabSeed("会議", "かいぎ", "meeting", "meeting", PartOfSpeech.NOUN),
                            VocabSeed("開始", "かいし", "start", "start", PartOfSpeech.NOUN),
                            VocabSeed("終了", "しゅうりょう", "finish", "finish", PartOfSpeech.NOUN),
                            VocabSeed("遅れる", "おくれる", "be late", "be late", PartOfSpeech.VERB),
                            VocabSeed("間に合う", "まにあう", "be on time", "be on time", PartOfSpeech.VERB),
                            VocabSeed("休み", "やすみ", "holiday", "holiday", PartOfSpeech.NOUN),
                            VocabSeed("平日", "へいじつ", "weekday", "weekday", PartOfSpeech.NOUN),
                            VocabSeed("毎日", "まいにち", "every day", "every day", PartOfSpeech.NOUN),
                            VocabSeed("来週", "らいしゅう", "next week", "next week", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Directions",
                description = "Asking for and giving directions.",
                totalDays = 6,
                courses = listOf(
                    CourseSeed(
                        title = "Basic Directions",
                        description = "Left, right, straight.",
                        vocabularies = listOf(
                            VocabSeed("右", "みぎ", "right", "right", PartOfSpeech.NOUN),
                            VocabSeed("左", "ひだり", "left", "left", PartOfSpeech.NOUN),
                            VocabSeed("まっすぐ", "まっすぐ", "straight", "straight", PartOfSpeech.ADV),
                            VocabSeed("曲がる", "まがる", "turn", "turn", PartOfSpeech.VERB),
                            VocabSeed("近い", "ちかい", "near", "near", PartOfSpeech.ADJ),
                            VocabSeed("遠い", "とおい", "far", "far", PartOfSpeech.ADJ),
                            VocabSeed("道", "みち", "road", "road", PartOfSpeech.NOUN),
                            VocabSeed("角", "かど", "corner", "corner", PartOfSpeech.NOUN),
                            VocabSeed("交差点", "こうさてん", "intersection", "intersection", PartOfSpeech.NOUN),
                            VocabSeed("信号", "しんごう", "traffic light", "traffic light", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Places",
                        description = "Common places.",
                        vocabularies = listOf(
                            VocabSeed("駅", "えき", "station", "station", PartOfSpeech.NOUN),
                            VocabSeed("銀行", "ぎんこう", "bank", "bank", PartOfSpeech.NOUN),
                            VocabSeed("病院", "びょういん", "hospital", "hospital", PartOfSpeech.NOUN),
                            VocabSeed("郵便局", "ゆうびんきょく", "post office", "post office", PartOfSpeech.NOUN),
                            VocabSeed("コンビニ", "こんびに", "convenience store", "convenience store", PartOfSpeech.NOUN),
                            VocabSeed("学校", "がっこう", "school", "school", PartOfSpeech.NOUN),
                            VocabSeed("公園", "こうえん", "park", "park", PartOfSpeech.NOUN),
                            VocabSeed("店", "みせ", "shop", "shop", PartOfSpeech.NOUN),
                            VocabSeed("ホテル", "ほてる", "hotel", "hotel", PartOfSpeech.NOUN),
                            VocabSeed("トイレ", "といれ", "toilet", "toilet", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Asking for Help",
                        description = "Phrases for asking directions.",
                        vocabularies = listOf(
                            VocabSeed("ここ", "ここ", "here", "here", PartOfSpeech.PRON),
                            VocabSeed("そこ", "そこ", "there", "there", PartOfSpeech.PRON),
                            VocabSeed("あそこ", "あそこ", "over there", "over there", PartOfSpeech.PRON),
                            VocabSeed("どちら", "どちら", "which way", "which way", PartOfSpeech.PRON),
                            VocabSeed("教える", "おしえる", "teach/tell", "tell", PartOfSpeech.VERB),
                            VocabSeed("聞く", "きく", "ask", "ask", PartOfSpeech.VERB),
                            VocabSeed("助ける", "たすける", "help", "help", PartOfSpeech.VERB),
                            VocabSeed("迷う", "まよう", "get lost", "get lost", PartOfSpeech.VERB),
                            VocabSeed("地図", "ちず", "map", "map", PartOfSpeech.NOUN),
                            VocabSeed("案内", "あんない", "guide", "guide", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(privateSyllabus, privateTopicSeeds)
    }
}
