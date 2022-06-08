package com.example.pigeneratorflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/*
1 вопрос) Handler - это механизм, который позволяет работать с очередью сообщений.
Он привязан к конкретному потоку (thread) и работает с его очередью. Handler умеет
помещать сообщения в очередь. При этом он ставит самого себя в качестве получателя этого сообщения.
И когда приходит время, система достает сообщение из очереди и отправляет его
адресату (т.е. в Handler) на обработку.
Handler дает нам две интересные и полезные возможности:
1) реализовать отложенное по времени выполнение кода
2) выполнение кода не в своем потоке

2 вопрос) Корутины помогают писать асинхронный код синхронно. Android — это однопоточная платформа,
и по умолчанию все работает на основном потоке (потоке UI). Когда настает время запускать операции,
несвязанные с UI (например, сетевой вызов, работа с БД, I/O операции или прием задачи
в любой момент), мы распределяем задачи по различным потокам и, если нужно, передаем результат
обратно в поток UI.С корутиной код выглядит легче. Нам не нужно использовать обратный вызов,
и следующая строка будет выполнена, как только придет ответ.

Минусы:
Недоконца обкатана, как новая технология
Java мир привык к блокировкам, поэтому неблокирующих библиотек мало


3 вопрос) Раньше обычно что бы обеспечить работу асинхронно, в основном использовали RxJava.
Теперь Kotlin Jetbrains придумали Flow с помощью него мы можем последовательно обрабатывать
поток данных
Flow — лучший способ асинхронно обрабатывать поток данных, который выполняется последовательно.
Используя Flow для обработки потоков значений, мы можем преобразовывать данные сложными
многопоточными способами, написав всего лишь небольшой фрагмент кода. С помощью Flow можно
отправлять запросы на сервер или в базу данных без блокирования основного потока приложения.

 */

class MainActivity : AppCompatActivity(), FragmentTimeCounter.SendMessageListener {
    private var fragmentCalculatorPI: FragmentCalculatorPI? = null
    private var fragmentTimeCounter: FragmentTimeCounter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentCalculatorPI = FragmentCalculatorPI()
        fragmentTimeCounter = FragmentTimeCounter()

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_calculator, fragmentCalculatorPI!!)
            .replace(R.id.container_time_counter, fragmentTimeCounter!!)
            .commit()
    }

    override fun onMessageDetails(message: String) {
        val calculatorPI: FragmentCalculatorPI? = supportFragmentManager
            .findFragmentById(R.id.container_calculator) as FragmentCalculatorPI?
        if (calculatorPI != null) {
            when (message) {
                getString(R.string.message_start) -> calculatorPI.onClickStart()
                getString(R.string.message_restart) -> calculatorPI.onClickRestart()
                getString(R.string.message_pause) -> calculatorPI.onClickPause()
            }
        }
    }
}