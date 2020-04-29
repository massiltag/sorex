# README #

![Logo](https://bitbucket.org/maxtag/sorex/raw/3f5e09337bd4f47f2a41adb80163f78d40f0a4cc/img/logo/SorEx.png)  
SorEx is a Blockchain, made for educational purposes by Sorbonne University undergraduate students.
Supervised by [Maria POTOP-BUTUCARU](https://www.lip6.fr/actualite/personnes-fiche.php?ident=P246) (LIP6)


### Abstract ###

* Centralized  
![Illustration-1](https://bitbucket.org/maxtag/sorex/raw/b65141f735e75333de4ddfe2fdad4175133ec01c/img/server-demo.PNG)
* Block visualization  
![Illustration-2](https://bitbucket.org/maxtag/sorex/raw/b65141f735e75333de4ddfe2fdad4175133ec01c/img/visual-blocks-demo.PNG)

### How to install ###
* Compiling sources
```bash
cd src
javac -cp ./ util/*.java && javac -cp ./ blockchain/*.java && javac -cp ./ architecture/*.java
```
* Launching Client
```bash
java architecture/SorexClient
```
* Launching Server
```bash
java architecture/SorexServer
```

### Author ###

[Massil Taguemout](https://www.linkedin.com/in/mtag/)  
Apprenti Développeur SI @ [GRTgaz](http://www.grtgaz.com/)  
Licence DANT, [Sorbonne Université](http://www.sorbonne-universite.fr/)
