package dice

object Render {
  import com.cibo.evilplot.colors.HTMLNamedColors.{green, red}
  import com.cibo.evilplot.geometry.Extent
  import com.cibo.evilplot._
  import com.cibo.evilplot.plot._
  import com.cibo.evilplot.plot.aesthetics.DefaultTheme._
  import com.cibo.evilplot.plot.renderers.BarRenderer

  val plotAreaSize: Extent = Extent(1000, 600)

  def apply(d: Dice) =
    displayPlot(
      BarChart(d.histogram)
        .xAxis()
        .yAxis()
        .xLabel("Value")
        .yLabel("Probability").frame())
}
