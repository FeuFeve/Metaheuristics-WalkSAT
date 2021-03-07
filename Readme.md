# Codage d’une métaheuristique dédiée à SAT

Projet de métaheuristique dédiée à SAT réalisé durant le semestre 2 de notre Master Informatique IMAGINA.

Ce projet va permettre de mettre en pratique nos connaissances concernant les métaheuristiques.
Nous allons réaliser **l'exercice 2** de la feuille de TD suivante : http://www.lirmm.fr/~trombetton/cours/TD21.pdf

- Le support de cours : http://www.lirmm.fr/~trombetton/cours/local.pdf

## Auteurs

* **Clément Potin**
* **Romain Fournier**
* **Émery Bourget-Vecchio**
* **Melvin Bardin**
* **Malika Lin-wee-kuan**
* **Maël Bonneaud**



## Description de l'algorithme utilisé

L'algorithme ici implémenté est WalkSAT (cours slide 26). Il permet de trouver une solution, lorsqu'il en existe, à des formules au format CNF (Conjunctive Normal Form).

Nous avons décidé d'implémenter WalkSAT plutôt que GSAT pour sa meilleure rapidité d'exécution sur des séries comportant beaucoup de variables.

## Exécution

Pour lancer le projet, il suffit de lancer la classe **Main.java** (elle ne prend aucun argument).

Dans cette classe Main.java, il est possible de changer les lignes suivantes :

- Ligne 6 : le nom du fichier '.cnf' à charger
- Ligne 11 : Décommenter la ligne pour afficher la formule une fois parsée
- Ligne 15 : Décommenter pour lancer l'algorithme non-optimisé une fois
- Ligne 16 : Décommenter pour lancer l'algorithme optimisé une fois
- Ligne 18 : Lance un des deux algorithme un certain nombre de fois (détaillé par la suite)

L'arborescence du projet ressemble à la suivante :

```
Projet
|
|---src
|   |---main (dossier où sont situées les classes '.java')
|       |---Main.java
|       |---...
|
|---files (Dossier où sont situés les fichiers de test au format '.cnf')
    |---uf20-01.cnf
    |---...
```

## Description des différentes classes

### Classe Parser.java

La classe **Parser** lit un fichier au format '.cnf' et le transforme en objet SAT (voir classe 'SAT.java'). 

### Classe SAT.java

La classe **SAT** contient 3 méthodes principales :

- **randomWalkSAT** qui inverse une variable aléatoirement à chaque itération. Elle prend en arguments :
    - L'objet SAT (qui sera copié et non modifié)
    - Le nombre maximum d'itérations (l'algorithme s'arrête quand il a trouvé une solution ou quand il est arrivé à ce nombre maximum d'itérations)
    - Un booléen qui permet d'afficher le détail de l'exécution si mis à false

- **optimisedWalkSAT**, détaillé dans le cours slide 26, qui à chaque itération calcule un "break score" et inverse la variable qui a le plus petit, ou fait une inversion aléatoire en fonction d'une probabilité "randomProbability" passée en argument. Elle prend en arguments :
    - L'objet SAT (qui sera copié et non modifié)
    - Le nombre maximum d'itérations (l'algorithme s'arrête quand il a trouvé une solution ou quand il est arrivé à ce nombre maximum d'itérations)
    - La variable "randomProbability" ("p" dans le cours, nombre flottant entre 0 et 1) : la probabilité que l'inversion se fasse sur une variable aléatoire plutôt que sur la variable avec le plus petit break score
    - Un booléen qui permet d'afficher le détail de l'exécution si mis à false

- **repeatWalkSAT** qui va répéter l'une des deux méthodes précédentes un certain nombre de fois. Cela permet de trouver des solutions différentes si l'algorithme est bloqué. L'algorithme s'arrête si l'une des répétitions trouve une solution qui satisfait la formule. Elle prend en arguments :
    - L'objet SAT (qui sera copié et non modifié)
    - Le nombre de répétitions
    - Le nombre d'itérations par répétitions
    - Un booléen qui permet de décider quelle méthode utiliser : si mis à true ce sera la méthode **randomWalkSAT**, si mis à false la méthode **optimisedWalkSAT**

Chacune de ces méthodes retourne une copie modifiée de l'objet SAT passé en arguments. Cette copie est la solution (/solution la plus proche) trouvée par l'algorithme.

### Classe Clause.java

La classe **Clause** permet de créer des objets "Clause", contenant des littéraux (atomes). Exemple :

- Clause 24: !22 v !137 v 238

Ici la clause 24 est validée si les littéraux (atomes) 22 ou 137 sont à faux, ou si l'atome 238 est à vrai.

Elle contient pour ce faire une méthode **violation**, qui permet de déterminer si une assignation (liste d'atomes) viole ou non la clause actuelle. Si le moindre atome de la liste rend la clause vraie, alors la méthode retourne false (la clause est satisfaite et il n'y a **pas** de viol). Si aucun atome ne satisfait la clause, alors elle retourne true.

Cette méthode **violation** est utilisée par les méthodes de la classe **SAT** à chaque itération.

### Classe Atom.java

La classe **Atom** représente les littéraux des formules et clauses. Chaque atome a un identifiant (exemple : 22, 137 ou 238 dans l'exemple du dessus), et un booléen déterminant si l'atome est actuellement à vrai ou faux.

**Les atomes des clauses ne sont jamais changés** : les clauses restent telles quelles du début à la fin du programme, car elles représentent la formule CNF.

Par contre les atomes de la classe **SAT**, stockés dans une liste, peuvent eux changer. Ils représentent ici l'assignation courante, qui doit essayer de satisfaire chaque clause du problème (les clauses sont elles aussi stockées dans une liste de la classe **SAT**).
