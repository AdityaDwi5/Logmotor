package id.serviskus.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppContextInstrumentedTest {
    @Test
    fun applicationId_sesuai_konfigurasi_ServisKu() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("id.serviskus.app", appContext.packageName)
    }
}
