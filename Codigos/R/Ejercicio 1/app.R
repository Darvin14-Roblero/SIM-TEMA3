library(shiny)
library(ggplot2)

ui <- fluidPage(
  titlePanel("Simulador Integral: Variables Aleatorias en R"),
  
  tabsetPanel(
    # =========================================================================
    # 1. PESTAÑA: CONCEPTO GENERAL
    # =========================================================================
    tabPanel("1. Concepto General",
             br(),
             wellPanel(
               h3("¿Qué es una Variable Aleatoria?"),
               p("Una variable aleatoria X es una función que asigna un valor numérico a cada resultado de un experimento aleatorio."),
               hr(),
               tags$ul(
                 tags$li(strong("Variables Discretas:"), " Toman valores finitos o contables (ej. lanzar dados, conteo de defectos). Se caracterizan por su Función de Masa de Probabilidad P(X = x)."),
                 br(),
                 tags$li(strong("Variables Continuas:"), " Toman infinitos valores en un intervalo continuo (ej. tiempo, peso, estatura). Se caracterizan por su Función de Densidad de Probabilidad f(x).")
               ),
               p("Navega por las pestañas superiores para explorar cada tipo de variable de forma interactiva.")
             )
    ),
    
    # =========================================================================
    # 2. PESTAÑA: VARIABLE DISCRETA (Suma de Dados)
    # =========================================================================
    tabPanel("2. Variable Discreta (Suma de Dados)",
             br(),
             sidebarLayout(
               sidebarPanel(
                 numericInput("num_lanzamientos", "Número de lanzamientos:", value = 1000, min = 10, max = 50000, step = 100),
                 actionButton("btn_discreto", "Simular Lanzamientos", class = "btn-primary"),
                 hr(),
                 h4("Resumen Estadístico"),
                 textOutput("media_discreta"),
                 textOutput("media_teorica_discreta")
               ),
               mainPanel(
                 plotOutput("plot_discreto", height = "400px")
               )
             )
    ),
    
    # =========================================================================
    # 3. PESTAÑA: VARIABLE CONTINUA (Exponencial)
    # =========================================================================
    tabPanel("3. Variable Continua (Exponencial)",
             br(),
             sidebarLayout(
               sidebarPanel(
                 sliderInput("lambda_val", "Tasa (λ):", min = 0.1, max = 3.0, value = 1.0, step = 0.1),
                 selectInput("n_continua", "Muestra (N):", choices = c(100, 500, 1000, 5000, 10000), selected = 1000),
                 actionButton("btn_continuo", "Simular Variable Continua", class = "btn-success"),
                 hr(),
                 h4("Resumen Estadístico"),
                 textOutput("media_continua"),
                 textOutput("media_teorica_continua")
               ),
               mainPanel(
                 plotOutput("plot_pdf", height = "300px"),
                 br(),
                 plotOutput("plot_cdf", height = "300px")
               )
             )
    )
  )
)

server <- function(input, output, session) {
  
  # ---------------------------------------------------------------------------
  # LÓGICA DISCRETA
  # ---------------------------------------------------------------------------
  datos_discretos <- reactive({
    input$btn_discreto
    n <- isolate(input$num_lanzamientos)
    dado1 <- sample(1:6, size = n, replace = TRUE)
    dado2 <- sample(1:6, size = n, replace = TRUE)
    dado1 + dado2
  })
  
  output$media_discreta <- renderText({
    paste("Media Observada:", round(mean(datos_discretos()), 4))
  })
  
  output$media_teorica_discreta <- renderText({
    "Media Teórica E[X]: 7.0000"
  })
  
  output$plot_discreto <- renderPlot({
    res <- datos_discretos()
    df <- data.frame(Suma = factor(res, levels = 2:12))
    
    prob_teorica <- data.frame(
      Suma = factor(2:12),
      Prob = c(1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1) / 36
    )
    
    ggplot(df, aes(x = Suma)) +
      geom_bar(aes(y = ..prop.., group = 1), fill = "#3498db", alpha = 0.7, color = "black") +
      geom_point(data = prob_teorica, aes(x = Suma, y = Prob), color = "red", size = 3) +
      geom_line(data = prob_teorica, aes(x = as.numeric(Suma), y = Prob), color = "red", linetype = "dashed", size = 1) +
      labs(title = "Distribución Discreta: Suma de 2 Dados",
           subtitle = "Barras azules = Frecuencia Relativa Observada | Puntos rojos = Probabilidad Teórica",
           x = "Valor de la Variable (Suma)", y = "Probabilidad P(X = x)") +
      theme_minimal()
  })
  
  # ---------------------------------------------------------------------------
  # LÓGICA CONTINUA
  # ---------------------------------------------------------------------------
  datos_continuos <- reactive({
    input$btn_continuo
    n <- as.numeric(isolate(input$n_continua))
    lambda <- isolate(input$lambda_val)
    rexp(n, rate = lambda)
  })
  
  output$media_continua <- renderText({
    paste("Media Observada:", round(mean(datos_continuos()), 4))
  })
  
  output$media_teorica_continua <- renderText({
    lambda <- input$lambda_val
    paste("Media Teórica E[X]:", round(1 / lambda, 4))
  })
  
  output$plot_pdf <- renderPlot({
    datos <- datos_continuos()
    lambda <- input$lambda_val
    
    df <- data.frame(x = datos)
    x_seq <- seq(0, max(datos) + 1, length.out = 500)
    pdf_teorica <- data.frame(x = x_seq, y = dexp(x_seq, rate = lambda))
    
    ggplot(df, aes(x = x)) +
      geom_histogram(aes(y = ..density..), bins = 30, fill = "#2ecc71", alpha = 0.6, color = "white") +
      geom_line(data = pdf_teorica, aes(x = x, y = y), color = "red", size = 1.2) +
      labs(title = "Función de Densidad de Probabilidad (PDF)",
           x = "Valor (x)", y = "Densidad f(x)") +
      theme_minimal()
  })
  
  output$plot_cdf <- renderPlot({
    datos <- datos_continuos()
    lambda <- input$lambda_val
    
    df <- data.frame(x = datos)
    x_seq <- seq(0, max(datos) + 1, length.out = 500)
    cdf_teorica <- data.frame(x = x_seq, y = pexp(x_seq, rate = lambda))
    
    ggplot(df, aes(x = x)) +
      stat_ecdf(geom = "step", color = "#2ecc71", size = 1) +
      geom_line(data = cdf_teorica, aes(x = x, y = y), color = "red", linetype = "dashed", size = 1.2) +
      labs(title = "Función de Distribución Acumulada (CDF)",
           x = "Valor (x)", y = "Probabilidad P(X ≤ x)") +
      theme_minimal()
  })
}

shinyApp(ui = ui, server = server)