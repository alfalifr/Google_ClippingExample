package sidev.app.android.google_clippingexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity() {
  /**
   * {@inheritDoc}
   *
   * Perform initialization of all fragments.
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(ClippedView(this))
  }
}