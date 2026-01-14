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
        subscriptionRepository: SubscriptionRepository,
        paymentMethodRepository: PaymentMethodRepository,
        vocabularyTermRepository: VocabularyTermRepository,
        vocabularyMeaningRepository: VocabularyMeaningRepository,
        vocabularyMediaRepository: VocabularyMediaRepository,
    ) = ApplicationRunner {
        if (userRepository.count() == 0L) {
            val users = listOf(
                User(email = "vocafy.exesp26@gmail.com", role = Role.ADMIN, status = Status.ACTIVE),

                User(email = "khiemngse182188@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "manager2@vocafy.local", role = Role.MANAGER, status = Status.ACTIVE),
                User(email = "baoltgse182138@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE), // Manager 3 - Nguyễn Văn A
                User(email = "phatttse182221@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE), // Manager 4 - Trần Thị B
                User(email = "anltse184186@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE), // Manager 5 - Lê Văn C
                User(email = "sondtse183892@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE), // Manager 6 - Phạm Thị D
                User(email = "thaodpss170172@fpt.edu.vn", role = Role.MANAGER, status = Status.ACTIVE),
            
                
                User(email = "khiem1371@gmail.com", role = Role.USER, status = Status.ACTIVE),
                User(email = "giabaostrike2004@gmail.com", role = Role.USER, status = Status.ACTIVE),
                User(email = "user3@vocafy.local", role = Role.USER, status = Status.ACTIVE),
            
            )
            val savedUsers = userRepository.saveAll(users)
            val profiles = savedUsers.mapIndexed { index, user ->
                Profile(
                    user = user,
                    displayName = when (index) {
                        0 -> "Admin User"
                        1 -> "Manager One"
                        2 -> "Manager Two"
                        3 -> "Nguyễn Văn A"
                        4 -> "Trần Thị B"
                        5 -> "Lê Văn C"
                        6 -> "Phạm Thị D"
                        7 -> "Hoàng Văn E"
                        else -> "User ${index - 7}"
                    },
                )
            }
            userRepository.flush()
            profileRepository.saveAll(profiles)

            if (subscriptionRepository.count() == 0L) {
                val subscriptions = savedUsers.map { user ->
                    if (user.email == "khiem1371@gmail.com" && user.email == "giabaostrike2004@gmail.com") {
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
            vocabularyMediaRepository.count() > 0
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
                        url = "", // Để trống - thêm link sau
                    )
                )
            }

            vocabularyTermRepository.saveAll(terms)
            vocabularyMeaningRepository.saveAll(meanings)
            vocabularyMediaRepository.saveAll(medias)
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

        // ============ SYLLABUS JLPT N4 - Intermediate ============
        val n4Syllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N4 Intermediate",
                description = "Intermediate Japanese vocabulary for JLPT N4 level. Covers more complex grammar and expressions.",
                totalDays = 45,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PUBLIC,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val n4TopicSeeds = listOf(
            TopicSeed(
                title = "Family & Relationships",
                description = "Vocabulary about family members and relationships.",
                totalDays = 7,
                courses = listOf(
                    CourseSeed(
                        title = "Family Members",
                        description = "Words for family members.",
                        vocabularies = listOf(
                            VocabSeed("家族", "かぞく", "family", "family", PartOfSpeech.NOUN),
                            VocabSeed("両親", "りょうしん", "parents", "parents", PartOfSpeech.NOUN),
                            VocabSeed("父", "ちち", "father", "father (humble)", PartOfSpeech.NOUN),
                            VocabSeed("母", "はは", "mother", "mother (humble)", PartOfSpeech.NOUN),
                            VocabSeed("お父さん", "おとうさん", "father", "father (polite)", PartOfSpeech.NOUN),
                            VocabSeed("お母さん", "おかあさん", "mother", "mother (polite)", PartOfSpeech.NOUN),
                            VocabSeed("兄", "あに", "older brother", "older brother (humble)", PartOfSpeech.NOUN),
                            VocabSeed("姉", "あね", "older sister", "older sister (humble)", PartOfSpeech.NOUN),
                            VocabSeed("弟", "おとうと", "younger brother", "younger brother", PartOfSpeech.NOUN),
                            VocabSeed("妹", "いもうと", "younger sister", "younger sister", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Extended Family",
                        description = "Extended family members.",
                        vocabularies = listOf(
                            VocabSeed("祖父", "そふ", "grandfather", "grandfather (humble)", PartOfSpeech.NOUN),
                            VocabSeed("祖母", "そぼ", "grandmother", "grandmother (humble)", PartOfSpeech.NOUN),
                            VocabSeed("おじいさん", "おじいさん", "grandfather", "grandfather (polite)", PartOfSpeech.NOUN),
                            VocabSeed("おばあさん", "おばあさん", "grandmother", "grandmother (polite)", PartOfSpeech.NOUN),
                            VocabSeed("叔父", "おじ", "uncle", "uncle", PartOfSpeech.NOUN),
                            VocabSeed("叔母", "おば", "aunt", "aunt", PartOfSpeech.NOUN),
                            VocabSeed("いとこ", "いとこ", "cousin", "cousin", PartOfSpeech.NOUN),
                            VocabSeed("親戚", "しんせき", "relative", "relative", PartOfSpeech.NOUN),
                            VocabSeed("孫", "まご", "grandchild", "grandchild", PartOfSpeech.NOUN),
                            VocabSeed("子供", "こども", "child", "child", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Relationships",
                        description = "Relationship vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("友達", "ともだち", "friend", "friend", PartOfSpeech.NOUN),
                            VocabSeed("親友", "しんゆう", "best friend", "best friend", PartOfSpeech.NOUN),
                            VocabSeed("恋人", "こいびと", "lover", "lover/partner", PartOfSpeech.NOUN),
                            VocabSeed("彼氏", "かれし", "boyfriend", "boyfriend", PartOfSpeech.NOUN),
                            VocabSeed("彼女", "かのじょ", "girlfriend", "girlfriend", PartOfSpeech.NOUN),
                            VocabSeed("夫", "おっと", "husband", "husband", PartOfSpeech.NOUN),
                            VocabSeed("妻", "つま", "wife", "wife", PartOfSpeech.NOUN),
                            VocabSeed("結婚", "けっこん", "marriage", "marriage", PartOfSpeech.NOUN),
                            VocabSeed("離婚", "りこん", "divorce", "divorce", PartOfSpeech.NOUN),
                            VocabSeed("付き合う", "つきあう", "date", "to date/go out with", PartOfSpeech.VERB),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Health & Body",
                description = "Vocabulary about health and body parts.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Body Parts",
                        description = "Parts of the body.",
                        vocabularies = listOf(
                            VocabSeed("体", "からだ", "body", "body", PartOfSpeech.NOUN),
                            VocabSeed("頭", "あたま", "head", "head", PartOfSpeech.NOUN),
                            VocabSeed("髪", "かみ", "hair", "hair", PartOfSpeech.NOUN),
                            VocabSeed("目", "め", "eye", "eye", PartOfSpeech.NOUN),
                            VocabSeed("耳", "みみ", "ear", "ear", PartOfSpeech.NOUN),
                            VocabSeed("鼻", "はな", "nose", "nose", PartOfSpeech.NOUN),
                            VocabSeed("口", "くち", "mouth", "mouth", PartOfSpeech.NOUN),
                            VocabSeed("歯", "は", "tooth", "tooth", PartOfSpeech.NOUN),
                            VocabSeed("首", "くび", "neck", "neck", PartOfSpeech.NOUN),
                            VocabSeed("肩", "かた", "shoulder", "shoulder", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "More Body Parts",
                        description = "Additional body parts.",
                        vocabularies = listOf(
                            VocabSeed("腕", "うで", "arm", "arm", PartOfSpeech.NOUN),
                            VocabSeed("手", "て", "hand", "hand", PartOfSpeech.NOUN),
                            VocabSeed("指", "ゆび", "finger", "finger", PartOfSpeech.NOUN),
                            VocabSeed("足", "あし", "leg/foot", "leg/foot", PartOfSpeech.NOUN),
                            VocabSeed("背中", "せなか", "back", "back", PartOfSpeech.NOUN),
                            VocabSeed("お腹", "おなか", "stomach", "stomach", PartOfSpeech.NOUN),
                            VocabSeed("胸", "むね", "chest", "chest", PartOfSpeech.NOUN),
                            VocabSeed("心臓", "しんぞう", "heart", "heart (organ)", PartOfSpeech.NOUN),
                            VocabSeed("血", "ち", "blood", "blood", PartOfSpeech.NOUN),
                            VocabSeed("骨", "ほね", "bone", "bone", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Health & Illness",
                        description = "Health-related vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("病気", "びょうき", "illness", "illness/sick", PartOfSpeech.NOUN),
                            VocabSeed("風邪", "かぜ", "cold", "cold (illness)", PartOfSpeech.NOUN),
                            VocabSeed("熱", "ねつ", "fever", "fever", PartOfSpeech.NOUN),
                            VocabSeed("薬", "くすり", "medicine", "medicine", PartOfSpeech.NOUN),
                            VocabSeed("医者", "いしゃ", "doctor", "doctor", PartOfSpeech.NOUN),
                            VocabSeed("看護師", "かんごし", "nurse", "nurse", PartOfSpeech.NOUN),
                            VocabSeed("痛い", "いたい", "painful", "painful", PartOfSpeech.ADJ),
                            VocabSeed("具合", "ぐあい", "condition", "condition/health", PartOfSpeech.NOUN),
                            VocabSeed("治る", "なおる", "recover", "to recover", PartOfSpeech.VERB),
                            VocabSeed("入院", "にゅういん", "hospitalization", "hospitalization", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Symptoms",
                        description = "Common symptoms.",
                        vocabularies = listOf(
                            VocabSeed("咳", "せき", "cough", "cough", PartOfSpeech.NOUN),
                            VocabSeed("頭痛", "ずつう", "headache", "headache", PartOfSpeech.NOUN),
                            VocabSeed("腹痛", "ふくつう", "stomachache", "stomachache", PartOfSpeech.NOUN),
                            VocabSeed("吐き気", "はきけ", "nausea", "nausea", PartOfSpeech.NOUN),
                            VocabSeed("めまい", "めまい", "dizziness", "dizziness", PartOfSpeech.NOUN),
                            VocabSeed("疲れる", "つかれる", "get tired", "to get tired", PartOfSpeech.VERB),
                            VocabSeed("眠い", "ねむい", "sleepy", "sleepy", PartOfSpeech.ADJ),
                            VocabSeed("だるい", "だるい", "sluggish", "sluggish", PartOfSpeech.ADJ),
                            VocabSeed("怪我", "けが", "injury", "injury", PartOfSpeech.NOUN),
                            VocabSeed("アレルギー", "あれるぎー", "allergy", "allergy", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Weather & Seasons",
                description = "Vocabulary about weather and seasons.",
                totalDays = 6,
                courses = listOf(
                    CourseSeed(
                        title = "Weather",
                        description = "Weather vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("天気", "てんき", "weather", "weather", PartOfSpeech.NOUN),
                            VocabSeed("晴れ", "はれ", "sunny", "sunny/clear", PartOfSpeech.NOUN),
                            VocabSeed("曇り", "くもり", "cloudy", "cloudy", PartOfSpeech.NOUN),
                            VocabSeed("雨", "あめ", "rain", "rain", PartOfSpeech.NOUN),
                            VocabSeed("雪", "ゆき", "snow", "snow", PartOfSpeech.NOUN),
                            VocabSeed("風", "かぜ", "wind", "wind", PartOfSpeech.NOUN),
                            VocabSeed("台風", "たいふう", "typhoon", "typhoon", PartOfSpeech.NOUN),
                            VocabSeed("雷", "かみなり", "thunder", "thunder/lightning", PartOfSpeech.NOUN),
                            VocabSeed("降る", "ふる", "fall", "to fall (rain/snow)", PartOfSpeech.VERB),
                            VocabSeed("傘", "かさ", "umbrella", "umbrella", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Seasons",
                        description = "Seasons and related vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("季節", "きせつ", "season", "season", PartOfSpeech.NOUN),
                            VocabSeed("春", "はる", "spring", "spring", PartOfSpeech.NOUN),
                            VocabSeed("夏", "なつ", "summer", "summer", PartOfSpeech.NOUN),
                            VocabSeed("秋", "あき", "autumn", "autumn/fall", PartOfSpeech.NOUN),
                            VocabSeed("冬", "ふゆ", "winter", "winter", PartOfSpeech.NOUN),
                            VocabSeed("暖かい", "あたたかい", "warm", "warm", PartOfSpeech.ADJ),
                            VocabSeed("涼しい", "すずしい", "cool", "cool", PartOfSpeech.ADJ),
                            VocabSeed("暑い", "あつい", "hot", "hot (weather)", PartOfSpeech.ADJ),
                            VocabSeed("寒い", "さむい", "cold", "cold (weather)", PartOfSpeech.ADJ),
                            VocabSeed("湿度", "しつど", "humidity", "humidity", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Temperature & Climate",
                        description = "Temperature and climate words.",
                        vocabularies = listOf(
                            VocabSeed("気温", "きおん", "temperature", "temperature", PartOfSpeech.NOUN),
                            VocabSeed("度", "ど", "degree", "degree", PartOfSpeech.NOUN),
                            VocabSeed("蒸し暑い", "むしあつい", "humid", "hot and humid", PartOfSpeech.ADJ),
                            VocabSeed("乾燥", "かんそう", "dry", "dry/dryness", PartOfSpeech.NOUN),
                            VocabSeed("予報", "よほう", "forecast", "forecast", PartOfSpeech.NOUN),
                            VocabSeed("晴れる", "はれる", "clear up", "to clear up", PartOfSpeech.VERB),
                            VocabSeed("曇る", "くもる", "get cloudy", "to get cloudy", PartOfSpeech.VERB),
                            VocabSeed("止む", "やむ", "stop", "to stop (rain)", PartOfSpeech.VERB),
                            VocabSeed("霧", "きり", "fog", "fog", PartOfSpeech.NOUN),
                            VocabSeed("虹", "にじ", "rainbow", "rainbow", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Education & Study",
                description = "Vocabulary about education and studying.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "School",
                        description = "School vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("学校", "がっこう", "school", "school", PartOfSpeech.NOUN),
                            VocabSeed("大学", "だいがく", "university", "university", PartOfSpeech.NOUN),
                            VocabSeed("高校", "こうこう", "high school", "high school", PartOfSpeech.NOUN),
                            VocabSeed("中学校", "ちゅうがっこう", "middle school", "middle school", PartOfSpeech.NOUN),
                            VocabSeed("小学校", "しょうがっこう", "elementary school", "elementary school", PartOfSpeech.NOUN),
                            VocabSeed("教室", "きょうしつ", "classroom", "classroom", PartOfSpeech.NOUN),
                            VocabSeed("図書館", "としょかん", "library", "library", PartOfSpeech.NOUN),
                            VocabSeed("体育館", "たいいくかん", "gymnasium", "gymnasium", PartOfSpeech.NOUN),
                            VocabSeed("校長", "こうちょう", "principal", "principal", PartOfSpeech.NOUN),
                            VocabSeed("クラス", "くらす", "class", "class", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Subjects",
                        description = "School subjects.",
                        vocabularies = listOf(
                            VocabSeed("科目", "かもく", "subject", "subject", PartOfSpeech.NOUN),
                            VocabSeed("数学", "すうがく", "mathematics", "mathematics", PartOfSpeech.NOUN),
                            VocabSeed("英語", "えいご", "English", "English", PartOfSpeech.NOUN),
                            VocabSeed("国語", "こくご", "Japanese", "Japanese (subject)", PartOfSpeech.NOUN),
                            VocabSeed("理科", "りか", "science", "science", PartOfSpeech.NOUN),
                            VocabSeed("社会", "しゃかい", "social studies", "social studies", PartOfSpeech.NOUN),
                            VocabSeed("歴史", "れきし", "history", "history", PartOfSpeech.NOUN),
                            VocabSeed("地理", "ちり", "geography", "geography", PartOfSpeech.NOUN),
                            VocabSeed("音楽", "おんがく", "music", "music", PartOfSpeech.NOUN),
                            VocabSeed("美術", "びじゅつ", "art", "art", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Study Actions",
                        description = "Actions related to studying.",
                        vocabularies = listOf(
                            VocabSeed("勉強", "べんきょう", "study", "study", PartOfSpeech.NOUN),
                            VocabSeed("勉強する", "べんきょうする", "study", "to study", PartOfSpeech.VERB),
                            VocabSeed("習う", "ならう", "learn", "to learn", PartOfSpeech.VERB),
                            VocabSeed("教える", "おしえる", "teach", "to teach", PartOfSpeech.VERB),
                            VocabSeed("覚える", "おぼえる", "memorize", "to memorize", PartOfSpeech.VERB),
                            VocabSeed("忘れる", "わすれる", "forget", "to forget", PartOfSpeech.VERB),
                            VocabSeed("練習", "れんしゅう", "practice", "practice", PartOfSpeech.NOUN),
                            VocabSeed("復習", "ふくしゅう", "review", "review", PartOfSpeech.NOUN),
                            VocabSeed("予習", "よしゅう", "preparation", "preparation (study)", PartOfSpeech.NOUN),
                            VocabSeed("質問", "しつもん", "question", "question", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Exams & Grades",
                        description = "Exams and academic performance.",
                        vocabularies = listOf(
                            VocabSeed("試験", "しけん", "exam", "exam", PartOfSpeech.NOUN),
                            VocabSeed("テスト", "てすと", "test", "test", PartOfSpeech.NOUN),
                            VocabSeed("宿題", "しゅくだい", "homework", "homework", PartOfSpeech.NOUN),
                            VocabSeed("点数", "てんすう", "score", "score", PartOfSpeech.NOUN),
                            VocabSeed("成績", "せいせき", "grades", "grades", PartOfSpeech.NOUN),
                            VocabSeed("合格", "ごうかく", "pass", "to pass (exam)", PartOfSpeech.NOUN),
                            VocabSeed("不合格", "ふごうかく", "fail", "to fail (exam)", PartOfSpeech.NOUN),
                            VocabSeed("卒業", "そつぎょう", "graduation", "graduation", PartOfSpeech.NOUN),
                            VocabSeed("入学", "にゅうがく", "enrollment", "enrollment", PartOfSpeech.NOUN),
                            VocabSeed("答え", "こたえ", "answer", "answer", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Work & Career",
                description = "Vocabulary about work and careers.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Occupations",
                        description = "Various occupations.",
                        vocabularies = listOf(
                            VocabSeed("仕事", "しごと", "job", "job/work", PartOfSpeech.NOUN),
                            VocabSeed("会社員", "かいしゃいん", "office worker", "company employee", PartOfSpeech.NOUN),
                            VocabSeed("社長", "しゃちょう", "president", "company president", PartOfSpeech.NOUN),
                            VocabSeed("部長", "ぶちょう", "manager", "department manager", PartOfSpeech.NOUN),
                            VocabSeed("課長", "かちょう", "section chief", "section chief", PartOfSpeech.NOUN),
                            VocabSeed("弁護士", "べんごし", "lawyer", "lawyer", PartOfSpeech.NOUN),
                            VocabSeed("エンジニア", "えんじにあ", "engineer", "engineer", PartOfSpeech.NOUN),
                            VocabSeed("公務員", "こうむいん", "civil servant", "civil servant", PartOfSpeech.NOUN),
                            VocabSeed("警察官", "けいさつかん", "police officer", "police officer", PartOfSpeech.NOUN),
                            VocabSeed("消防士", "しょうぼうし", "firefighter", "firefighter", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Workplace",
                        description = "Workplace vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("会社", "かいしゃ", "company", "company", PartOfSpeech.NOUN),
                            VocabSeed("オフィス", "おふぃす", "office", "office", PartOfSpeech.NOUN),
                            VocabSeed("デスク", "ですく", "desk", "desk", PartOfSpeech.NOUN),
                            VocabSeed("パソコン", "ぱそこん", "computer", "personal computer", PartOfSpeech.NOUN),
                            VocabSeed("コピー機", "こぴーき", "copier", "copy machine", PartOfSpeech.NOUN),
                            VocabSeed("会議室", "かいぎしつ", "meeting room", "conference room", PartOfSpeech.NOUN),
                            VocabSeed("受付", "うけつけ", "reception", "reception", PartOfSpeech.NOUN),
                            VocabSeed("エレベーター", "えれべーたー", "elevator", "elevator", PartOfSpeech.NOUN),
                            VocabSeed("階段", "かいだん", "stairs", "stairs", PartOfSpeech.NOUN),
                            VocabSeed("駐車場", "ちゅうしゃじょう", "parking lot", "parking lot", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Work Actions",
                        description = "Work-related verbs.",
                        vocabularies = listOf(
                            VocabSeed("働く", "はたらく", "work", "to work", PartOfSpeech.VERB),
                            VocabSeed("出勤", "しゅっきん", "go to work", "going to work", PartOfSpeech.NOUN),
                            VocabSeed("退勤", "たいきん", "leave work", "leaving work", PartOfSpeech.NOUN),
                            VocabSeed("残業", "ざんぎょう", "overtime", "overtime work", PartOfSpeech.NOUN),
                            VocabSeed("出張", "しゅっちょう", "business trip", "business trip", PartOfSpeech.NOUN),
                            VocabSeed("休暇", "きゅうか", "vacation", "vacation/leave", PartOfSpeech.NOUN),
                            VocabSeed("給料", "きゅうりょう", "salary", "salary", PartOfSpeech.NOUN),
                            VocabSeed("昇進", "しょうしん", "promotion", "promotion", PartOfSpeech.NOUN),
                            VocabSeed("転職", "てんしょく", "job change", "change jobs", PartOfSpeech.NOUN),
                            VocabSeed("辞める", "やめる", "quit", "to quit", PartOfSpeech.VERB),
                        ),
                    ),
                    CourseSeed(
                        title = "Business Terms",
                        description = "Business-related terms.",
                        vocabularies = listOf(
                            VocabSeed("契約", "けいやく", "contract", "contract", PartOfSpeech.NOUN),
                            VocabSeed("商品", "しょうひん", "product", "product", PartOfSpeech.NOUN),
                            VocabSeed("サービス", "さーびす", "service", "service", PartOfSpeech.NOUN),
                            VocabSeed("顧客", "こきゃく", "customer", "customer", PartOfSpeech.NOUN),
                            VocabSeed("取引先", "とりひきさき", "client", "business partner", PartOfSpeech.NOUN),
                            VocabSeed("売上", "うりあげ", "sales", "sales", PartOfSpeech.NOUN),
                            VocabSeed("利益", "りえき", "profit", "profit", PartOfSpeech.NOUN),
                            VocabSeed("経費", "けいひ", "expenses", "expenses", PartOfSpeech.NOUN),
                            VocabSeed("プロジェクト", "ぷろじぇくと", "project", "project", PartOfSpeech.NOUN),
                            VocabSeed("締め切り", "しめきり", "deadline", "deadline", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Hobbies & Leisure",
                description = "Vocabulary about hobbies and leisure activities.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Sports",
                        description = "Sports vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("スポーツ", "すぽーつ", "sports", "sports", PartOfSpeech.NOUN),
                            VocabSeed("サッカー", "さっかー", "soccer", "soccer", PartOfSpeech.NOUN),
                            VocabSeed("野球", "やきゅう", "baseball", "baseball", PartOfSpeech.NOUN),
                            VocabSeed("テニス", "てにす", "tennis", "tennis", PartOfSpeech.NOUN),
                            VocabSeed("バスケ", "ばすけ", "basketball", "basketball", PartOfSpeech.NOUN),
                            VocabSeed("水泳", "すいえい", "swimming", "swimming", PartOfSpeech.NOUN),
                            VocabSeed("ゴルフ", "ごるふ", "golf", "golf", PartOfSpeech.NOUN),
                            VocabSeed("試合", "しあい", "match", "match/game", PartOfSpeech.NOUN),
                            VocabSeed("勝つ", "かつ", "win", "to win", PartOfSpeech.VERB),
                            VocabSeed("負ける", "まける", "lose", "to lose", PartOfSpeech.VERB),
                        ),
                    ),
                    CourseSeed(
                        title = "Entertainment",
                        description = "Entertainment vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("映画", "えいが", "movie", "movie", PartOfSpeech.NOUN),
                            VocabSeed("音楽", "おんがく", "music", "music", PartOfSpeech.NOUN),
                            VocabSeed("ゲーム", "げーむ", "game", "game", PartOfSpeech.NOUN),
                            VocabSeed("漫画", "まんが", "manga", "manga/comic", PartOfSpeech.NOUN),
                            VocabSeed("アニメ", "あにめ", "anime", "anime", PartOfSpeech.NOUN),
                            VocabSeed("ドラマ", "どらま", "drama", "TV drama", PartOfSpeech.NOUN),
                            VocabSeed("コンサート", "こんさーと", "concert", "concert", PartOfSpeech.NOUN),
                            VocabSeed("カラオケ", "からおけ", "karaoke", "karaoke", PartOfSpeech.NOUN),
                            VocabSeed("歌う", "うたう", "sing", "to sing", PartOfSpeech.VERB),
                            VocabSeed("踊る", "おどる", "dance", "to dance", PartOfSpeech.VERB),
                        ),
                    ),
                    CourseSeed(
                        title = "Outdoor Activities",
                        description = "Outdoor activities.",
                        vocabularies = listOf(
                            VocabSeed("旅行", "りょこう", "travel", "travel", PartOfSpeech.NOUN),
                            VocabSeed("キャンプ", "きゃんぷ", "camping", "camping", PartOfSpeech.NOUN),
                            VocabSeed("登山", "とざん", "hiking", "mountain climbing", PartOfSpeech.NOUN),
                            VocabSeed("釣り", "つり", "fishing", "fishing", PartOfSpeech.NOUN),
                            VocabSeed("散歩", "さんぽ", "walk", "walk/stroll", PartOfSpeech.NOUN),
                            VocabSeed("ジョギング", "じょぎんぐ", "jogging", "jogging", PartOfSpeech.NOUN),
                            VocabSeed("サイクリング", "さいくりんぐ", "cycling", "cycling", PartOfSpeech.NOUN),
                            VocabSeed("ピクニック", "ぴくにっく", "picnic", "picnic", PartOfSpeech.NOUN),
                            VocabSeed("バーベキュー", "ばーべきゅー", "barbecue", "barbecue", PartOfSpeech.NOUN),
                            VocabSeed("写真", "しゃしん", "photo", "photo", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Indoor Hobbies",
                        description = "Indoor hobbies.",
                        vocabularies = listOf(
                            VocabSeed("趣味", "しゅみ", "hobby", "hobby", PartOfSpeech.NOUN),
                            VocabSeed("読書", "どくしょ", "reading", "reading", PartOfSpeech.NOUN),
                            VocabSeed("料理", "りょうり", "cooking", "cooking", PartOfSpeech.NOUN),
                            VocabSeed("絵", "え", "drawing", "drawing/picture", PartOfSpeech.NOUN),
                            VocabSeed("書道", "しょどう", "calligraphy", "calligraphy", PartOfSpeech.NOUN),
                            VocabSeed("茶道", "さどう", "tea ceremony", "tea ceremony", PartOfSpeech.NOUN),
                            VocabSeed("園芸", "えんげい", "gardening", "gardening", PartOfSpeech.NOUN),
                            VocabSeed("編み物", "あみもの", "knitting", "knitting", PartOfSpeech.NOUN),
                            VocabSeed("楽器", "がっき", "instrument", "musical instrument", PartOfSpeech.NOUN),
                            VocabSeed("ピアノ", "ぴあの", "piano", "piano", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(n4Syllabus, n4TopicSeeds)

        // ============ SYLLABUS Business Japanese ============
        val businessSyllabus = syllabusRepository.save(
            Syllabus(
                title = "Business Japanese",
                description = "Essential Japanese vocabulary for business communication and professional settings.",
                totalDays = 30,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PUBLIC,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val businessTopicSeeds = listOf(
            TopicSeed(
                title = "Business Etiquette",
                description = "Professional greetings and etiquette.",
                totalDays = 5,
                courses = listOf(
                    CourseSeed(
                        title = "Business Greetings",
                        description = "Formal greetings in business.",
                        vocabularies = listOf(
                            VocabSeed("お疲れ様です", "おつかれさまです", "good work", "thank you for your work", PartOfSpeech.INTERJ),
                            VocabSeed("ご苦労様です", "ごくろうさまです", "thanks for your effort", "thank you for your trouble", PartOfSpeech.INTERJ),
                            VocabSeed("失礼します", "しつれいします", "excuse me", "excuse me (entering/leaving)", PartOfSpeech.INTERJ),
                            VocabSeed("よろしくお願いします", "よろしくおねがいします", "please", "I look forward to working with you", PartOfSpeech.INTERJ),
                            VocabSeed("お世話になります", "おせわになります", "thank you", "thank you for your help", PartOfSpeech.INTERJ),
                            VocabSeed("申し訳ございません", "もうしわけございません", "I'm sorry", "I'm very sorry (formal)", PartOfSpeech.INTERJ),
                            VocabSeed("恐れ入ります", "おそれいります", "sorry to bother", "I'm sorry to trouble you", PartOfSpeech.INTERJ),
                            VocabSeed("かしこまりました", "かしこまりました", "understood", "certainly (formal)", PartOfSpeech.INTERJ),
                            VocabSeed("承知しました", "しょうちしました", "understood", "I understand (formal)", PartOfSpeech.INTERJ),
                            VocabSeed("ご連絡ください", "ごれんらくください", "please contact", "please contact me", PartOfSpeech.INTERJ),
                        ),
                    ),
                    CourseSeed(
                        title = "Business Cards",
                        description = "Exchanging business cards.",
                        vocabularies = listOf(
                            VocabSeed("名刺", "めいし", "business card", "business card", PartOfSpeech.NOUN),
                            VocabSeed("名刺交換", "めいしこうかん", "card exchange", "business card exchange", PartOfSpeech.NOUN),
                            VocabSeed("肩書き", "かたがき", "title", "job title", PartOfSpeech.NOUN),
                            VocabSeed("役職", "やくしょく", "position", "position", PartOfSpeech.NOUN),
                            VocabSeed("所属", "しょぞく", "affiliation", "affiliation/department", PartOfSpeech.NOUN),
                            VocabSeed("連絡先", "れんらくさき", "contact", "contact information", PartOfSpeech.NOUN),
                            VocabSeed("頂戴します", "ちょうだいします", "receive", "I humbly receive", PartOfSpeech.VERB),
                            VocabSeed("拝見します", "はいけんします", "look at", "I humbly look at", PartOfSpeech.VERB),
                            VocabSeed("初めまして", "はじめまして", "nice to meet you", "nice to meet you", PartOfSpeech.INTERJ),
                            VocabSeed("ご紹介", "ごしょうかい", "introduction", "introduction", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Meetings",
                description = "Vocabulary for business meetings.",
                totalDays = 6,
                courses = listOf(
                    CourseSeed(
                        title = "Meeting Basics",
                        description = "Basic meeting vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("会議", "かいぎ", "meeting", "meeting", PartOfSpeech.NOUN),
                            VocabSeed("打ち合わせ", "うちあわせ", "briefing", "meeting/briefing", PartOfSpeech.NOUN),
                            VocabSeed("議題", "ぎだい", "agenda", "agenda", PartOfSpeech.NOUN),
                            VocabSeed("議事録", "ぎじろく", "minutes", "meeting minutes", PartOfSpeech.NOUN),
                            VocabSeed("資料", "しりょう", "materials", "materials/documents", PartOfSpeech.NOUN),
                            VocabSeed("発表", "はっぴょう", "presentation", "presentation", PartOfSpeech.NOUN),
                            VocabSeed("提案", "ていあん", "proposal", "proposal", PartOfSpeech.NOUN),
                            VocabSeed("意見", "いけん", "opinion", "opinion", PartOfSpeech.NOUN),
                            VocabSeed("賛成", "さんせい", "agree", "agreement", PartOfSpeech.NOUN),
                            VocabSeed("反対", "はんたい", "oppose", "opposition", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Meeting Phrases",
                        description = "Useful meeting phrases.",
                        vocabularies = listOf(
                            VocabSeed("ご確認ください", "ごかくにんください", "please confirm", "please confirm", PartOfSpeech.INTERJ),
                            VocabSeed("ご検討ください", "ごけんとうください", "please consider", "please consider", PartOfSpeech.INTERJ),
                            VocabSeed("ご質問", "ごしつもん", "question", "question (polite)", PartOfSpeech.NOUN),
                            VocabSeed("ご意見", "ごいけん", "opinion", "opinion (polite)", PartOfSpeech.NOUN),
                            VocabSeed("まとめ", "まとめ", "summary", "summary", PartOfSpeech.NOUN),
                            VocabSeed("結論", "けつろん", "conclusion", "conclusion", PartOfSpeech.NOUN),
                            VocabSeed("決定", "けってい", "decision", "decision", PartOfSpeech.NOUN),
                            VocabSeed("延期", "えんき", "postpone", "postponement", PartOfSpeech.NOUN),
                            VocabSeed("中止", "ちゅうし", "cancel", "cancellation", PartOfSpeech.NOUN),
                            VocabSeed("参加", "さんか", "participate", "participation", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Email & Communication",
                description = "Email and business communication.",
                totalDays = 6,
                courses = listOf(
                    CourseSeed(
                        title = "Email Writing",
                        description = "Email vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("件名", "けんめい", "subject", "subject line", PartOfSpeech.NOUN),
                            VocabSeed("添付", "てんぷ", "attachment", "attachment", PartOfSpeech.NOUN),
                            VocabSeed("送信", "そうしん", "send", "send", PartOfSpeech.NOUN),
                            VocabSeed("受信", "じゅしん", "receive", "receive", PartOfSpeech.NOUN),
                            VocabSeed("返信", "へんしん", "reply", "reply", PartOfSpeech.NOUN),
                            VocabSeed("転送", "てんそう", "forward", "forward", PartOfSpeech.NOUN),
                            VocabSeed("CC", "しーしー", "CC", "carbon copy", PartOfSpeech.NOUN),
                            VocabSeed("宛先", "あてさき", "recipient", "recipient", PartOfSpeech.NOUN),
                            VocabSeed("署名", "しょめい", "signature", "signature", PartOfSpeech.NOUN),
                            VocabSeed("お忙しいところ", "おいそがしいところ", "sorry to bother", "despite your busy schedule", PartOfSpeech.INTERJ),
                        ),
                    ),
                    CourseSeed(
                        title = "Phone Etiquette",
                        description = "Phone conversation vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("電話", "でんわ", "phone", "telephone", PartOfSpeech.NOUN),
                            VocabSeed("内線", "ないせん", "extension", "extension number", PartOfSpeech.NOUN),
                            VocabSeed("外線", "がいせん", "outside line", "outside line", PartOfSpeech.NOUN),
                            VocabSeed("留守電", "るすでん", "voicemail", "voicemail", PartOfSpeech.NOUN),
                            VocabSeed("折り返し", "おりかえし", "callback", "callback", PartOfSpeech.NOUN),
                            VocabSeed("伝言", "でんごん", "message", "message", PartOfSpeech.NOUN),
                            VocabSeed("お待たせしました", "おまたせしました", "sorry for waiting", "sorry to keep you waiting", PartOfSpeech.INTERJ),
                            VocabSeed("少々お待ちください", "しょうしょうおまちください", "please wait", "please wait a moment", PartOfSpeech.INTERJ),
                            VocabSeed("お電話ください", "おでんわください", "please call", "please call me", PartOfSpeech.INTERJ),
                            VocabSeed("お掛け直しください", "おかけなおしください", "please call again", "please call back", PartOfSpeech.INTERJ),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Reports & Documents",
                description = "Document and report vocabulary.",
                totalDays = 5,
                courses = listOf(
                    CourseSeed(
                        title = "Documents",
                        description = "Document types.",
                        vocabularies = listOf(
                            VocabSeed("書類", "しょるい", "documents", "documents", PartOfSpeech.NOUN),
                            VocabSeed("報告書", "ほうこくしょ", "report", "report", PartOfSpeech.NOUN),
                            VocabSeed("企画書", "きかくしょ", "proposal", "proposal document", PartOfSpeech.NOUN),
                            VocabSeed("見積書", "みつもりしょ", "estimate", "estimate/quote", PartOfSpeech.NOUN),
                            VocabSeed("請求書", "せいきゅうしょ", "invoice", "invoice", PartOfSpeech.NOUN),
                            VocabSeed("領収書", "りょうしゅうしょ", "receipt", "receipt", PartOfSpeech.NOUN),
                            VocabSeed("申請書", "しんせいしょ", "application", "application form", PartOfSpeech.NOUN),
                            VocabSeed("承認", "しょうにん", "approval", "approval", PartOfSpeech.NOUN),
                            VocabSeed("却下", "きゃっか", "rejection", "rejection", PartOfSpeech.NOUN),
                            VocabSeed("提出", "ていしゅつ", "submission", "submission", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Report Writing",
                        description = "Report writing vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("目的", "もくてき", "purpose", "purpose", PartOfSpeech.NOUN),
                            VocabSeed("背景", "はいけい", "background", "background", PartOfSpeech.NOUN),
                            VocabSeed("方法", "ほうほう", "method", "method", PartOfSpeech.NOUN),
                            VocabSeed("結果", "けっか", "result", "result", PartOfSpeech.NOUN),
                            VocabSeed("考察", "こうさつ", "discussion", "discussion/analysis", PartOfSpeech.NOUN),
                            VocabSeed("課題", "かだい", "issue", "issue/task", PartOfSpeech.NOUN),
                            VocabSeed("対策", "たいさく", "countermeasure", "countermeasure", PartOfSpeech.NOUN),
                            VocabSeed("改善", "かいぜん", "improvement", "improvement", PartOfSpeech.NOUN),
                            VocabSeed("進捗", "しんちょく", "progress", "progress", PartOfSpeech.NOUN),
                            VocabSeed("達成", "たっせい", "achievement", "achievement", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Negotiations",
                description = "Negotiation and deal vocabulary.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Negotiation Terms",
                        description = "Negotiation vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("交渉", "こうしょう", "negotiation", "negotiation", PartOfSpeech.NOUN),
                            VocabSeed("条件", "じょうけん", "condition", "condition/terms", PartOfSpeech.NOUN),
                            VocabSeed("価格", "かかく", "price", "price", PartOfSpeech.NOUN),
                            VocabSeed("割引", "わりびき", "discount", "discount", PartOfSpeech.NOUN),
                            VocabSeed("納期", "のうき", "delivery date", "delivery date", PartOfSpeech.NOUN),
                            VocabSeed("保証", "ほしょう", "guarantee", "guarantee", PartOfSpeech.NOUN),
                            VocabSeed("取引", "とりひき", "transaction", "transaction/deal", PartOfSpeech.NOUN),
                            VocabSeed("妥協", "だきょう", "compromise", "compromise", PartOfSpeech.NOUN),
                            VocabSeed("合意", "ごうい", "agreement", "agreement", PartOfSpeech.NOUN),
                            VocabSeed("署名", "しょめい", "sign", "signature", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Contract Terms",
                        description = "Contract vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("契約", "けいやく", "contract", "contract", PartOfSpeech.NOUN),
                            VocabSeed("契約書", "けいやくしょ", "contract document", "contract document", PartOfSpeech.NOUN),
                            VocabSeed("期間", "きかん", "period", "period", PartOfSpeech.NOUN),
                            VocabSeed("更新", "こうしん", "renewal", "renewal", PartOfSpeech.NOUN),
                            VocabSeed("解約", "かいやく", "cancellation", "cancellation", PartOfSpeech.NOUN),
                            VocabSeed("違約金", "いやくきん", "penalty", "penalty fee", PartOfSpeech.NOUN),
                            VocabSeed("責任", "せきにん", "responsibility", "responsibility", PartOfSpeech.NOUN),
                            VocabSeed("義務", "ぎむ", "obligation", "obligation", PartOfSpeech.NOUN),
                            VocabSeed("権利", "けんり", "right", "right", PartOfSpeech.NOUN),
                            VocabSeed("秘密保持", "ひみつほじ", "confidentiality", "confidentiality", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(businessSyllabus, businessTopicSeeds)

        // ============ JLPT N3 (PRIVATE) ============
        val n3Syllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N3",
                description = "Upper-intermediate Japanese vocabulary for JLPT N3 level.",
                totalDays = 50,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val n3TopicSeeds = listOf(
            TopicSeed(
                title = "Communication",
                description = "Polite forms, requests, and suggestions.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Polite Forms",
                        description = "Politeness and respect terms.",
                        vocabularies = listOf(
                            VocabSeed("丁寧", "ていねい", "polite", "polite", PartOfSpeech.ADJ),
                            VocabSeed("謙遜", "けんそん", "humility", "humility", PartOfSpeech.NOUN),
                            VocabSeed("尊敬", "そんけい", "respect", "respect", PartOfSpeech.NOUN),
                            VocabSeed("断る", "ことわる", "refuse", "to refuse", PartOfSpeech.VERB),
                            VocabSeed("了承", "りょうしょう", "acknowledgement", "consent/acknowledgement", PartOfSpeech.NOUN),
                            VocabSeed("配慮", "はいりょ", "consideration", "consideration", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Requests & Suggestions",
                        description = "Requests, suggestions, and advice.",
                        vocabularies = listOf(
                            VocabSeed("依頼", "いらい", "request", "request", PartOfSpeech.NOUN),
                            VocabSeed("提案", "ていあん", "proposal", "suggestion/proposal", PartOfSpeech.NOUN),
                            VocabSeed("助言", "じょげん", "advice", "advice", PartOfSpeech.NOUN),
                            VocabSeed("相談する", "そうだんする", "consult", "to consult", PartOfSpeech.VERB),
                            VocabSeed("許可", "きょか", "permission", "permission", PartOfSpeech.NOUN),
                            VocabSeed("同意", "どうい", "consent", "agreement/consent", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Daily Situations",
                description = "Public facilities and emergencies.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Public Facilities",
                        description = "Common public services.",
                        vocabularies = listOf(
                            VocabSeed("役所", "やくしょ", "government office", "city/ward office", PartOfSpeech.NOUN),
                            VocabSeed("保健所", "ほけんじょ", "public health center", "health center", PartOfSpeech.NOUN),
                            VocabSeed("図書館", "としょかん", "library", "library", PartOfSpeech.NOUN),
                            VocabSeed("税務署", "ぜいむしょ", "tax office", "tax office", PartOfSpeech.NOUN),
                            VocabSeed("手続き", "てつづき", "procedure", "procedure", PartOfSpeech.NOUN),
                            VocabSeed("申請", "しんせい", "application", "application", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Emergencies",
                        description = "Emergency situations and responses.",
                        vocabularies = listOf(
                            VocabSeed("救急", "きゅうきゅう", "first aid", "emergency/first aid", PartOfSpeech.NOUN),
                            VocabSeed("通報", "つうほう", "report", "to report to authorities", PartOfSpeech.NOUN),
                            VocabSeed("避難", "ひなん", "evacuation", "evacuation", PartOfSpeech.NOUN),
                            VocabSeed("火災", "かさい", "fire", "fire (disaster)", PartOfSpeech.NOUN),
                            VocabSeed("事故", "じこ", "accident", "accident", PartOfSpeech.NOUN),
                            VocabSeed("連絡先", "れんらくさき", "contact", "contact information", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(n3Syllabus, n3TopicSeeds)

        // ============ JLPT N2 (PRIVATE) ============
        val n2Syllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N2",
                description = "Advanced Japanese vocabulary for JLPT N2 level.",
                totalDays = 60,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val n2TopicSeeds = listOf(
            TopicSeed(
                title = "Business & News",
                description = "Business terminology and media language.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "Business Terms",
                        description = "Advanced business vocabulary.",
                        vocabularies = listOf(
                            VocabSeed("企業", "きぎょう", "enterprise", "enterprise/company", PartOfSpeech.NOUN),
                            VocabSeed("景気", "けいき", "economy", "economic conditions", PartOfSpeech.NOUN),
                            VocabSeed("投資", "とうし", "investment", "investment", PartOfSpeech.NOUN),
                            VocabSeed("雇用", "こよう", "employment", "employment", PartOfSpeech.NOUN),
                            VocabSeed("需要", "じゅよう", "demand", "demand", PartOfSpeech.NOUN),
                            VocabSeed("供給", "きょうきゅう", "supply", "supply", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Media & News",
                        description = "Vocabulary in news reports.",
                        vocabularies = listOf(
                            VocabSeed("報道", "ほうどう", "reporting", "news reporting", PartOfSpeech.NOUN),
                            VocabSeed("世論", "よろん", "public opinion", "public opinion", PartOfSpeech.NOUN),
                            VocabSeed("速報", "そくほう", "breaking news", "news flash", PartOfSpeech.NOUN),
                            VocabSeed("取材", "しゅざい", "coverage", "news coverage/interview", PartOfSpeech.NOUN),
                            VocabSeed("編集", "へんしゅう", "editing", "editing", PartOfSpeech.NOUN),
                            VocabSeed("掲載", "けいさい", "publication", "publication", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Abstract Concepts",
                description = "Society, economy, and technology.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "Society & Economy",
                        description = "Abstract societal terms.",
                        vocabularies = listOf(
                            VocabSeed("格差", "かくさ", "disparity", "gap/disparity", PartOfSpeech.NOUN),
                            VocabSeed("平等", "びょうどう", "equality", "equality", PartOfSpeech.NOUN),
                            VocabSeed("福祉", "ふくし", "welfare", "social welfare", PartOfSpeech.NOUN),
                            VocabSeed("賛成", "さんせい", "approval", "support/approval", PartOfSpeech.NOUN),
                            VocabSeed("反対", "はんたい", "opposition", "opposition", PartOfSpeech.NOUN),
                            VocabSeed("規制", "きせい", "regulation", "regulation", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Technology",
                        description = "Tech-related advanced terms.",
                        vocabularies = listOf(
                            VocabSeed("革新", "かくしん", "innovation", "innovation", PartOfSpeech.NOUN),
                            VocabSeed("導入", "どうにゅう", "implementation", "introduction/implementation", PartOfSpeech.NOUN),
                            VocabSeed("普及", "ふきゅう", "diffusion", "spread/adoption", PartOfSpeech.NOUN),
                            VocabSeed("性能", "せいのう", "performance", "performance", PartOfSpeech.NOUN),
                            VocabSeed("互換性", "ごかんせい", "compatibility", "compatibility", PartOfSpeech.NOUN),
                            VocabSeed("最適化", "さいてきか", "optimization", "optimization", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(n2Syllabus, n2TopicSeeds)

        // ============ JLPT N1 (PRIVATE) ============
        val n1Syllabus = syllabusRepository.save(
            Syllabus(
                title = "JLPT N1",
                description = "Advanced/proficient Japanese vocabulary for JLPT N1 level.",
                totalDays = 70,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val n1TopicSeeds = listOf(
            TopicSeed(
                title = "Advanced Abstract",
                description = "Philosophy, logic, and policy.",
                totalDays = 12,
                courses = listOf(
                    CourseSeed(
                        title = "Philosophy & Logic",
                        description = "Highly abstract concepts.",
                        vocabularies = listOf(
                            VocabSeed("概念", "がいねん", "concept", "concept", PartOfSpeech.NOUN),
                            VocabSeed("仮説", "かせつ", "hypothesis", "hypothesis", PartOfSpeech.NOUN),
                            VocabSeed("論証", "ろんしょう", "proof", "logical proof", PartOfSpeech.NOUN),
                            VocabSeed("矛盾", "むじゅん", "contradiction", "contradiction", PartOfSpeech.NOUN),
                            VocabSeed("整合性", "せいごうせい", "consistency", "consistency", PartOfSpeech.NOUN),
                            VocabSeed("妥当性", "だとうせい", "validity", "validity", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Law & Policy",
                        description = "Legal and policy terms.",
                        vocabularies = listOf(
                            VocabSeed("立法", "りっぽう", "legislation", "legislation", PartOfSpeech.NOUN),
                            VocabSeed("行政", "ぎょうせい", "administration", "administration", PartOfSpeech.NOUN),
                            VocabSeed("司法", "しほう", "judiciary", "judicial branch", PartOfSpeech.NOUN),
                            VocabSeed("判例", "はんれい", "precedent", "case law", PartOfSpeech.NOUN),
                            VocabSeed("規範", "きはん", "norm", "norm/standard", PartOfSpeech.NOUN),
                            VocabSeed("権限", "けんげん", "authority", "authority/power", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Academic & Research",
                description = "Research and statistics vocabulary.",
                totalDays = 12,
                courses = listOf(
                    CourseSeed(
                        title = "Research",
                        description = "Academic research terms.",
                        vocabularies = listOf(
                            VocabSeed("調査", "ちょうさ", "survey", "investigation/survey", PartOfSpeech.NOUN),
                            VocabSeed("解析", "かいせき", "analysis", "analysis", PartOfSpeech.NOUN),
                            VocabSeed("検証", "けんしょう", "verification", "verification", PartOfSpeech.NOUN),
                            VocabSeed("仮定", "かてい", "assumption", "assumption", PartOfSpeech.NOUN),
                            VocabSeed("引用", "いんよう", "citation", "citation", PartOfSpeech.NOUN),
                            VocabSeed("結論", "けつろん", "conclusion", "conclusion", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Statistics",
                        description = "Statistical terms.",
                        vocabularies = listOf(
                            VocabSeed("平均", "へいきん", "average", "mean/average", PartOfSpeech.NOUN),
                            VocabSeed("分布", "ぶんぷ", "distribution", "distribution", PartOfSpeech.NOUN),
                            VocabSeed("標本", "ひょうほん", "sample", "sample", PartOfSpeech.NOUN),
                            VocabSeed("母集団", "ぼしゅうだん", "population", "population", PartOfSpeech.NOUN),
                            VocabSeed("相関", "そうかん", "correlation", "correlation", PartOfSpeech.NOUN),
                            VocabSeed("分散", "ぶんさん", "variance", "variance", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(n1Syllabus, n1TopicSeeds)

        // ============ Software Engineering (PUBLIC) ============
        val swEngSyllabus = syllabusRepository.save(
            Syllabus(
                title = "Software Engineering",
                description = "BE/FE/BA/Test domain vocabulary.",
                totalDays = 60,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val swEngTopicSeeds = listOf(
            TopicSeed(
                title = "Backend (BE)",
                description = "Server, API và dữ liệu.",
                totalDays = 12,
                courses = listOf(
                    CourseSeed(
                        title = "Database",
                        description = "Khái niệm cơ sở dữ liệu.",
                        vocabularies = listOf(
                            VocabSeed("スキーマ", "すきーま", "schema", "schema", PartOfSpeech.NOUN),
                            VocabSeed("テーブル", "てーぶる", "table", "table", PartOfSpeech.NOUN),
                            VocabSeed("主キー", "しゅきー", "primary key", "primary key", PartOfSpeech.NOUN),
                            VocabSeed("外部キー", "がいぶきー", "foreign key", "foreign key", PartOfSpeech.NOUN),
                            VocabSeed("インデックス", "いんでっくす", "index", "index", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "REST API",
                        description = "Thiết kế API và HTTP.",
                        vocabularies = listOf(
                            VocabSeed("エンドポイント", "えんどぽいんと", "endpoint", "endpoint", PartOfSpeech.NOUN),
                            VocabSeed("HTTP", "えいちてぃーてぃーぴー", "HTTP", "http", PartOfSpeech.NOUN),
                            VocabSeed("認証", "にんしょう", "authentication", "authentication", PartOfSpeech.NOUN),
                            VocabSeed("認可", "にんか", "authorization", "authorization", PartOfSpeech.NOUN),
                            VocabSeed("ステータスコード", "すてーたすこーど", "status code", "status code", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Frontend (FE)",
                description = "UI và trình duyệt.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "HTML & CSS",
                        description = "Đánh dấu và style cơ bản.",
                        vocabularies = listOf(
                            VocabSeed("HTML", "えいちてぃーえむえる", "HTML", "markup language", PartOfSpeech.NOUN),
                            VocabSeed("CSS", "しーえすえす", "CSS", "stylesheet", PartOfSpeech.NOUN),
                            VocabSeed("タグ", "たぐ", "tag", "tag", PartOfSpeech.NOUN),
                            VocabSeed("クラス", "くらす", "class", "css class", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Business Analyst (BA)",
                description = "Phân tích yêu cầu.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Requirements",
                        description = "Khái niệm yêu cầu.",
                        vocabularies = listOf(
                            VocabSeed("要件", "ようけん", "requirements", "requirements", PartOfSpeech.NOUN),
                            VocabSeed("ユーザーストーリー", "ゆーざーすとーりー", "user story", "user story", PartOfSpeech.NOUN),
                            VocabSeed("受入基準", "うけいれきじゅん", "acceptance criteria", "acceptance criteria", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Testing (QA)",
                description = "Kiểm thử phần mềm.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Testing Types",
                        description = "Các loại kiểm thử.",
                        vocabularies = listOf(
                            VocabSeed("単体試験", "たんたいしけん", "unit test", "unit test", PartOfSpeech.NOUN),
                            VocabSeed("結合試験", "けつごうしけん", "integration test", "integration test", PartOfSpeech.NOUN),
                            VocabSeed("回帰試験", "かいきしけん", "regression test", "regression test", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(swEngSyllabus, swEngTopicSeeds)

        // ============ AI Engineering (PUBLIC) ============
        val aiEngSyllabus = syllabusRepository.save(
            Syllabus(
                title = "AI Engineering",
                description = "ML/DL/MLOps domain vocabulary.",
                totalDays = 45,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val aiEngTopicSeeds = listOf(
            TopicSeed(
                title = "ML Fundamentals",
                description = "Khái niệm học máy cơ bản.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "Supervised Learning",
                        description = "Các khái niệm chính.",
                        vocabularies = listOf(
                            VocabSeed("データセット", "でーたせっと", "dataset", "dataset", PartOfSpeech.NOUN),
                            VocabSeed("特徴量", "とくちょうりょう", "feature", "feature", PartOfSpeech.NOUN),
                            VocabSeed("ラベル", "らべる", "label", "label", PartOfSpeech.NOUN),
                            VocabSeed("損失関数", "そんしつかんすう", "loss function", "loss function", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Deep Learning",
                description = "Mạng nơ-ron và tối ưu.",
                totalDays = 10,
                courses = listOf(
                    CourseSeed(
                        title = "Neural Networks",
                        description = "Khái niệm DL cơ bản.",
                        vocabularies = listOf(
                            VocabSeed("ニューラルネットワーク", "にゅーらるねっとわーく", "neural network", "neural network", PartOfSpeech.NOUN),
                            VocabSeed("活性化関数", "かっせいかかんすう", "activation function", "activation function", PartOfSpeech.NOUN),
                            VocabSeed("勾配降下", "こうばいこうか", "gradient descent", "gradient descent", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "MLOps",
                        description = "Triển khai và vận hành mô hình.",
                        vocabularies = listOf(
                            VocabSeed("モデルデプロイ", "もでるでぷろい", "model deployment", "model deployment", PartOfSpeech.NOUN),
                            VocabSeed("監視", "かんし", "monitoring", "monitoring", PartOfSpeech.NOUN),
                            VocabSeed("パイプライン", "ぱいぷらいん", "pipeline", "pipeline", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(aiEngSyllabus, aiEngTopicSeeds)

        // ============ Test Engineering (PUBLIC) ============
        val testEngSyllabus = syllabusRepository.save(
            Syllabus(
                title = "Test Engineering",
                description = "Testing processes and automation vocabulary.",
                totalDays = 35,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PUBLIC,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val testEngTopicSeeds = listOf(
            TopicSeed(
                title = "Testing Fundamentals",
                description = "Các khái niệm kiểm thử.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Artifacts",
                        description = "Tạo tác kiểm thử.",
                        vocabularies = listOf(
                            VocabSeed("テスト計画", "てすとけいかく", "test plan", "test plan", PartOfSpeech.NOUN),
                            VocabSeed("テスト設計", "てすとせっけい", "test design", "test design", PartOfSpeech.NOUN),
                            VocabSeed("テストケース", "てすとけーす", "test case", "test case", PartOfSpeech.NOUN),
                            VocabSeed("テストデータ", "てすとでーた", "test data", "test data", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Automation",
                description = "Tự động hóa kiểm thử.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Automation Basics",
                        description = "Khái niệm và công cụ.",
                        vocabularies = listOf(
                            VocabSeed("テスト自動化", "てすとじどうか", "test automation", "test automation", PartOfSpeech.NOUN),
                            VocabSeed("フレームワーク", "ふれーむわーく", "framework", "framework", PartOfSpeech.NOUN),
                            VocabSeed("レポート", "れぽーと", "report", "report", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(testEngSyllabus, testEngTopicSeeds)

        // ============ Data Engineering (PUBLIC) ============
        val dataEngSyllabus = syllabusRepository.save(
            Syllabus(
                title = "Data Engineering",
                description = "Modeling, ETL, Warehousing vocabulary.",
                totalDays = 40,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PUBLIC,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val dataEngTopicSeeds = listOf(
            TopicSeed(
                title = "Data Modeling",
                description = "Mô hình dữ liệu và tối ưu.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Modeling Basics",
                        description = "Khái niệm cốt lõi.",
                        vocabularies = listOf(
                            VocabSeed("スキーマ", "すきーま", "schema", "schema", PartOfSpeech.NOUN),
                            VocabSeed("正規化", "せいきか", "normalization", "normalization", PartOfSpeech.NOUN),
                            VocabSeed("ER図", "いーあーるず", "ER diagram", "er diagram", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "ETL Pipelines",
                description = "Luồng xử lý dữ liệu.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "ETL",
                        description = "Các bước ETL.",
                        vocabularies = listOf(
                            VocabSeed("抽出", "ちゅうしゅつ", "extract", "extract", PartOfSpeech.NOUN),
                            VocabSeed("変換", "へんかん", "transform", "transform", PartOfSpeech.NOUN),
                            VocabSeed("ロード", "ろーど", "load", "load", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
            TopicSeed(
                title = "Warehousing",
                description = "Kho dữ liệu và tối ưu truy vấn.",
                totalDays = 8,
                courses = listOf(
                    CourseSeed(
                        title = "Warehouse Basics",
                        description = "Khái niệm kho dữ liệu.",
                        vocabularies = listOf(
                            VocabSeed("データウェアハウス", "でーたうぇあはうす", "data warehouse", "data warehouse", PartOfSpeech.NOUN),
                            VocabSeed("クエリ", "くえり", "query", "query", PartOfSpeech.NOUN),
                            VocabSeed("最適化", "さいてきか", "optimization", "optimization", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(dataEngSyllabus, dataEngTopicSeeds)

        // ============ International Business (PUBLIC) ============
        val ibSyllabus = syllabusRepository.save(
            Syllabus(
                title = "International Business",
                description = "Trade, logistics, and finance vocabulary.",
                totalDays = 40,
                languageSet = LanguageSet.EN_JP,
                visibility = SyllabusVisibility.PRIVATE,
                sourceType = SyllabusSourceType.CURATED,
                createdBy = owner,
                active = true,
            )
        )

        val ibTopicSeeds = listOf(
            TopicSeed(
                title = "Trade Specialist",
                description = "Trade terms, logistics, and finance.",
                totalDays = 12,
                courses = listOf(
                    CourseSeed(
                        title = "Trade Terms",
                        description = "Incoterms và thuật ngữ thương mại.",
                        vocabularies = listOf(
                            VocabSeed("輸出", "ゆしゅつ", "export", "export", PartOfSpeech.NOUN),
                            VocabSeed("輸入", "ゆにゅう", "import", "import", PartOfSpeech.NOUN),
                            VocabSeed("関税", "かんぜい", "tariff", "tariff", PartOfSpeech.NOUN),
                            VocabSeed("インコタームズ", "いんこたーむず", "Incoterms", "incoterms", PartOfSpeech.NOUN),
                            VocabSeed("FOB", "えふおーびー", "FOB", "free on board", PartOfSpeech.NOUN),
                            VocabSeed("CIF", "しーあいえふ", "CIF", "cost insurance freight", PartOfSpeech.NOUN),
                            VocabSeed("原産地証明", "げんさんちしょうめい", "certificate of origin", "certificate of origin", PartOfSpeech.NOUN),
                            VocabSeed("請求書", "せいきゅうしょ", "invoice", "invoice", PartOfSpeech.NOUN),
                            VocabSeed("梱包", "こんぽう", "packaging", "packaging", PartOfSpeech.NOUN),
                            VocabSeed("船荷証券", "ふなにしょうけん", "bill of lading", "bill of lading", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Logistics",
                        description = "Vận chuyển và chuỗi cung ứng.",
                        vocabularies = listOf(
                            VocabSeed("物流", "ぶつりゅう", "logistics", "logistics", PartOfSpeech.NOUN),
                            VocabSeed("サプライチェーン", "さぷらいちぇーん", "supply chain", "supply chain", PartOfSpeech.NOUN),
                            VocabSeed("在庫", "ざいこ", "inventory", "inventory", PartOfSpeech.NOUN),
                            VocabSeed("倉庫", "そうこ", "warehouse", "warehouse", PartOfSpeech.NOUN),
                            VocabSeed("配送", "はいそう", "delivery", "delivery", PartOfSpeech.NOUN),
                            VocabSeed("通関", "つうかん", "customs clearance", "customs clearance", PartOfSpeech.NOUN),
                            VocabSeed("フォワーダー", "ふぉわーだー", "forwarder", "freight forwarder", PartOfSpeech.NOUN),
                            VocabSeed("トラッキング", "とらっきんぐ", "tracking", "shipment tracking", PartOfSpeech.NOUN),
                            VocabSeed("リードタイム", "りーどたいむ", "lead time", "lead time", PartOfSpeech.NOUN),
                            VocabSeed("需要予測", "じゅようよそく", "demand forecasting", "demand forecasting", PartOfSpeech.NOUN),
                        ),
                    ),
                    CourseSeed(
                        title = "Finance",
                        description = "Tài chính trong thương mại quốc tế.",
                        vocabularies = listOf(
                            VocabSeed("信用状", "しんようじょう", "letter of credit", "letter of credit", PartOfSpeech.NOUN),
                            VocabSeed("為替", "かわせ", "exchange", "foreign exchange", PartOfSpeech.NOUN),
                            VocabSeed("送金", "そうきん", "remittance", "remittance", PartOfSpeech.NOUN),
                            VocabSeed("保証", "ほしょう", "guarantee", "guarantee", PartOfSpeech.NOUN),
                            VocabSeed("利息", "りそく", "interest", "interest", PartOfSpeech.NOUN),
                            VocabSeed("為替レート", "かわせれーと", "exchange rate", "exchange rate", PartOfSpeech.NOUN),
                            VocabSeed("決済", "けっさい", "settlement", "settlement", PartOfSpeech.NOUN),
                            VocabSeed("融資", "ゆうし", "financing", "financing", PartOfSpeech.NOUN),
                            VocabSeed("資金調達", "しきんちょうたつ", "fundraising", "fundraising", PartOfSpeech.NOUN),
                            VocabSeed("信用調査", "しんようちょうさ", "credit check", "credit investigation", PartOfSpeech.NOUN),
                        ),
                    ),
                ),
            ),
        )

        seedSyllabusContent(ibSyllabus, ibTopicSeeds)
    }
}
