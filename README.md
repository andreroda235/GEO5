# APDC-GeoProject

Objectivo Geral do Projecto:

Links Importantes:
- Pagina do Projecto:

- Projecto do GitHub:https://github.com/Geo5Solutions/APDC-GeoProject.git

- Drive do Grupo: https://drive.google.com/drive/folders/1m_1fnkTN3BnlxCz17lSJtxRTqSerRnLW

- Parte do Cliente https://github.com/denisov93/angulargeo5

- Parte do Servidor https://github.com/admendes/Geo5-Server

- Parte de Android https://github.com/andreroda1/Geo5-Android.git


Processo de Deploy de Aplicação Web Consiste em:

 - 1
 
  - 1.1  Tirar do repositorio do servidor o projecto
  
  - 1.2  Tirar do repositorio do cliente o projecto
  
  - 1.3  instalar CLI na maquina. Ir a directoria do Projecto Cliente e executar comando ( ng build --prod ) que vai gerar um build do projeto
 
 - 2
 
 - 2.1  Abrir projecto do Servidor em ambiente Eclipse
 
 - 2.2  Corrigir alguns erros com comando maven update
 
 - 2.3 - dentro da pasta WEBAPP do Projecto colar conteudo gerado do build na alinea 1.3
 
 - 3-Fazer Deploy com Google App Engine 
