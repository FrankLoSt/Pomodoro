package com.example.pomodoro.ui.pickmonster

import com.example.pomodoro.R

import com.example.pomodoro.ui.statistics.TopMonsterData

data class MonsterState(
    val toggleSetUp: Boolean = false,
    val monsterPickedIndex: Int = 0,
    val monsterList: List<MonsterInfo> = listOf(
        MonsterInfo(R.drawable.warrior2, "Uncontrolled rage", "Absolutely, Cactus — here's a deeper dive into the monster of **Uncontrolled Rage**, imagined as a pixelated embodiment of emotional chaos:\n" +
                "\n" +
                "---\n" +
                "\n" +
                " \uD83D\uDD25 Monster Profile: Uncontrolled Rage\n" +
                "\n" +
                "Name: Uncontrolled Rage  \n" +
                "Appearance: A towering warrior with blazing eyes and clenched fists, wrapped in flickering red energy. His armor is cracked from past battles, and his roar echoes like a storm through the soul.\n" +
                "\n" +
                "Origin: Born from suppressed emotions and unresolved trauma, Uncontrolled Rage emerges when boundaries are crossed, patience is tested, and the heart is wounded repeatedly. He feeds on injustice, betrayal, and the feeling of being unheard.\n" +
                "\n" +
                "Behavior:  \n" +
                "- Strikes without warning, often triggered by minor frustrations that mask deeper pain  \n" +
                "- Overrides logic and empathy, replacing them with impulsive action  \n" +
                "- Leaves destruction in his wake — broken relationships, shattered trust, and lingering regret\n" +
                "\n" +
                "Abilities:  \n" +
                "- Impulse Surge: Forces the host to act before thinking, often saying or doing things they later regret  \n" +
                "- Tunnel Vision: Blocks out reason and alternative perspectives, making the host feel justified in their fury  \n" +
                "- Emotional Aftershock: Even after the rage subsides, the damage remains — apologies may not be enough to undo the harm\n" +
                "\n" +
                "Weaknesses:  \n" +
                "- Vulnerable to reflection, mindfulness, and emotional regulation  \n" +
                "- Loses power when the host learns to pause, breathe, and name their feelings  \n" +
                "- Can be tamed through therapy, journaling, and honest conversations\n" +
                "\n" +
                "Impact on the Host:  \n" +
                "Uncontrolled Rage isolates its host. Friends and loved ones begin to walk on eggshells, fearing the next outburst. Over time, the host may feel ashamed, misunderstood, or even haunted by their own actions. The monster thrives in silence and denial — but shrinks when the host chooses vulnerability over violence.\n" +
                "\n" +
                "---\n"),
        MonsterInfo(imageId = R.drawable.treemonster, "Bed rotting", "rotting your future and health"),
        MonsterInfo(R.drawable.spider, "Distraction", "Makes everyday tasks feel overwhelming"),
        MonsterInfo(R.drawable.monster1, "Porn addiction", "Drain your energy and destroy your relationship"),
        MonsterInfo(R.drawable._01_1, "Anxiety", "Makes everyday tasks feel overwhelming"),
        MonsterInfo(R.drawable._01_2, "Loneliness", "Leads to isolation and low self-worth"),
        MonsterInfo(R.drawable._02_2, "Burnout", "Kills motivation and joy in learning"),
        MonsterInfo(R.drawable._03_2, "Comparison", "Breeds insecurity through social media"),
        MonsterInfo(R.drawable._04_1, "Rejection", "Shakes confidence and self-image"),
        MonsterInfo(R.drawable._03_3, "Pressure", "Creates fear of failure and perfectionism"),
        MonsterInfo(R.drawable._06_2, "Procrastination", "Delays growth and builds guilt"),
        MonsterInfo(R.drawable._07_2, "Identity", "Confuses self-understanding and belonging"),
        MonsterInfo(R.drawable._08_2, "Addiction", "Distracts from goals and relationships"),
        MonsterInfo(R.drawable._09_2, "Bullying", "Damages trust and emotional safety"),
        MonsterInfo(R.drawable._10_2, "Self-Doubt", "Blocks ambition and creativity"),
        MonsterInfo(R.drawable._12_1, "Financial Stress", "Limits opportunity and causes anxiety"),
        MonsterInfo(R.drawable._07_3, "Overthinking", "Paralyzes decision-making"),
        MonsterInfo(R.drawable._13_2, "Imposter", "Makes success feel undeserved"),
        MonsterInfo(R.drawable._08_3, "Neglect", "Leaves emotional needs unmet"),
        MonsterInfo(R.drawable._11_1, "Fear", "Prevents risk-taking and growth"),
        MonsterInfo(R.drawable._14_1, "Toxic Positivity", "Invalidates real emotions"),
        MonsterInfo(R.drawable._15_1, "Distraction", "Scatters focus and productivity"),
        MonsterInfo(R.drawable._16_3, "Insecurity", "Erodes confidence and self-love"),
        MonsterInfo(R.drawable._14_3, "Perfectionism", "Turns effort into self-criticism"),
        MonsterInfo(R.drawable._18_2, "Isolation", "Disconnects from support systems"),
        MonsterInfo(R.drawable._19_2, "Uncertainty", "Creates anxiety about the future"),
        MonsterInfo(R.drawable._20_1, "Regret", "Chains you to the past")
    ),
    val tag: String = "Study",
    val top10: List<TopMonsterData> = listOf(
        TopMonsterData("Focus Fiend", 320, 18, 14, 4),
        TopMonsterData("Procrastino", 150, 10, 6, 4),
        TopMonsterData("Grind Goblin", 480, 25, 20, 5),
        TopMonsterData("Task Titan", 275, 12, 9, 3),
        TopMonsterData("Study Serpent", 360, 20, 15, 5)
    ),


    //Least and Most
    val mostFocusedDay: FocusedDay = FocusedDay("Monday", 120),
    val mostFocusedHour: FocusedHour = FocusedHour("16", 120),
    val mostFocusedDayOfMonth: FocusedDayOfMonth = FocusedDayOfMonth(10, 120),
    val leastFocusedDay: FocusedDay = FocusedDay("Monday", 10),
    val leastFocusedDayOfMonth: FocusedDayOfMonth = FocusedDayOfMonth(10, 10),
)




data class FocusedHour(val hourOnly: String, val  avgTime: Int)
data class FocusedDay(val dayOfWeek: String, val avgTime: Int)
data class FocusedDayOfMonth(val dayOfMonth: Int, val avgTime: Int)
