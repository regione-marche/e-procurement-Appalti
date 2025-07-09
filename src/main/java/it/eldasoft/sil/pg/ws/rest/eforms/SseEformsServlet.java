package it.eldasoft.sil.pg.ws.rest.eforms;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/sse-stream")
public class SseEformsServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
  private static final List<PrintWriter> clients = new CopyOnWriteArrayList<>();

  private static final Logger logger = Logger.getLogger(SseEformsServlet.class.getName());

  public static void notifyClients(String message) {
      queue.offer(message);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      response.setContentType("text/event-stream");
      response.setCharacterEncoding("UTF-8");

      PrintWriter writer = response.getWriter();
      clients.add(writer);

      try {
          while (true) {
              String message = queue.take();
              for (PrintWriter client : clients) {
                  client.write("data: " + message + "\n\n");
                  client.flush();
              }
          }
      } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          logger.log(Level.SEVERE, "Thread interrupted", e);
      } finally {
          clients.remove(writer);
          if (writer != null) {
              writer.close();
          }
      }
  }
}
