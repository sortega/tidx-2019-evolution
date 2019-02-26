package com.tidx.commons
import java.io.{BufferedReader, InputStreamReader}

abstract class CliApp extends App {
  private var _shouldStop: Boolean = false

  protected def shouldStop: Boolean = synchronized(_shouldStop)

  protected def run(): Unit

  private val thread = new Thread(() => {
    new BufferedReader(new InputStreamReader(System.in)).readLine()
    synchronized(_shouldStop = true)
    println("Stopping...")
  })

  thread.start()
  run()
  thread.join()
}
