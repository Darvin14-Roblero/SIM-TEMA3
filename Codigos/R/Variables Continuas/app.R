library(shiny)
library(ggplot2)

ui <- fluidPage(
  titlePanel("Variable Aleatoria Continua - Distribución Exponencial (R)"),
  
  sidebarLayout(
    sidebarPanel(
      sliderInput("lambda", 
                  "Tasa (λ):", 
                  min = 0.1, 
                  max = 3.0, 
                  value = 1.0, 
                  step = 0.1),
      
      selectInput("n", 
                  "Número de datos (N):", 
                  choices = c(100, 500, 1000, 5000, 10000), 
                  selected = 1000),
      
      actionButton("simular", "Simular Nuevamente", class = "btn-primary"),
      
      hr(),
      h4("Comparativa de Media"),
      textOutput("media_teorica"),
      textOutput("media_muestral")
    ),
    
    mainPanel(
      plotOutput("pdf_plot", height = "300px"),
      br(),
      plotOutput("cdf_plot", height = "300px")
    )
  )
)

server <- function(input, output, session) {
  
  datos_simulados <- reactive({
    input$simular
    n <- as.numeric(isolate(input$n))
    lambda <- isolate(input$lambda)
    
    rexp(n, rate = lambda)
  })
  
  output$media_teorica <- renderText({
    lambda <- input$lambda
    paste("Media Teórica E[X]:", round(1 / lambda, 4))
  })
  
  output$media_muestral <- renderText({
    datos <- datos_simulados()
    paste("Media Muestral (Simulada):", round(mean(datos), 4))
  })
  
  output$pdf_plot <- renderPlot({
    datos <- datos_simulados()
    lambda <- input$lambda
    
    df <- data.frame(x = datos)
    x_seq <- seq(0, max(datos) + 1, length.out = 500)
    pdf_teorica <- data.frame(x = x_seq, y = dexp(x_seq, rate = lambda))
    
    ggplot(df, aes(x = x)) +
      geom_histogram(aes(y = ..density..), bins = 30, fill = "#3498db", alpha = 0.6, color = "white") +
      geom_line(data = pdf_teorica, aes(x = x, y = y), color = "red", size = 1.2) +
      labs(title = "Función de Densidad de Probabilidad (PDF)",
           x = "Tiempo / Valor (x)", y = "Densidad f(x)") +
      theme_minimal()
  })
  
  output$cdf_plot <- renderPlot({
    datos <- datos_simulados()
    lambda <- input$lambda
    
    df <- data.frame(x = datos)
    x_seq <- seq(0, max(datos) + 1, length.out = 500)
    cdf_teorica <- data.frame(x = x_seq, y = pexp(x_seq, rate = lambda))
    
    ggplot(df, aes(x = x)) +
      stat_ecdf(geom = "step", color = "#2ecc71", size = 1) +
      geom_line(data = cdf_teorica, aes(x = x, y = y), color = "red", linetype = "dashed", size = 1.2) +
      labs(title = "Función de Distribución Acumulada (CDF)",
           x = "Tiempo / Valor (x)", y = "Probabilidad P(X ≤ x)") +
      theme_minimal()
  })
}

shinyApp(ui = ui, server = server)

