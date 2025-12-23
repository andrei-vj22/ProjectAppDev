package com.example.projectappdev

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.Calendar as JavaCalendar

class Calendar : AppCompatActivity() {

    lateinit var calendarView: MaterialCalendarView
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        calendarView = findViewById(R.id.calendarView)
        val totalJournalsTxt: TextView = findViewById(R.id.textView12)
        val btnBack: ImageView = findViewById(R.id.img_goback2)

        btnBack.setOnClickListener { finish() }

        // Fetch data from Firestore
        loadJournalData(totalJournalsTxt)

        calendarView.setOnDateChangedListener { _, date, _ ->
            Toast.makeText(this, "Date: ${date.day}/${date.month}/${date.year}", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadJournalData(totalJournalsTxt: TextView) {
        db.collection("tbl_entries")
            .get()
            .addOnSuccessListener { result ->
                totalJournalsTxt.text = result.size().toString()

                val entryDates = mutableSetOf<CalendarDay>()

                for (document in result) {
                    val timestamp = document.getTimestamp("date")
                    if (timestamp != null) {
                        val calendarDay = convertTimestampToCalendarDay(timestamp)
                        entryDates.add(calendarDay)
                    }
                }

                if (entryDates.isNotEmpty()) {
                    calendarView.addDecorator(JournalDateDecorator(Color.RED, entryDates))
                    calendarView.invalidateDecorators() // Forces the dots to draw
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun convertTimestampToCalendarDay(timestamp: Timestamp): CalendarDay {
        val date = timestamp.toDate()
        val cal = JavaCalendar.getInstance()
        cal.time = date
        // MaterialCalendarView uses 1-based months in many versions
        return CalendarDay.from(
            cal.get(JavaCalendar.YEAR),
            cal.get(JavaCalendar.MONTH) + 1,
            cal.get(JavaCalendar.DAY_OF_MONTH)
        )
    }
}

// Keeping the decorator in the same file, outside the Activity class
class JournalDateDecorator(val color: Int, val dates: Collection<CalendarDay>) : DayViewDecorator {
    val dateSet = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay): Boolean = dateSet.contains(day)

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(10f, color))
    }
}