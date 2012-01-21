#!/usr/bin/env python
#
#Original code by Nicolas Parisot
#svn checkout http://projet3bim.googlecode.com/svn/trunk/ projet3bim-read-only
#Modified and Translated by Mauricio Esguerra
#Date: January 19, 2012
import numpy

# Declare Constants
APP_AU=-3 # 
APP_GC=-4 # 
APP_GU=0 # 
TAILLE_BOUCLE=4 # Minimal number of bases in a loop

# Method to output the pairing value between "a" and "b"
def Match2(a,b):
    if((a[0]=="A" and b[0]=="U") or (a[0]=="U" and b[0]=="A")):
        return APP_AU
    elif((a[0]=="G" and b[0]=="C") or (a[0]=="C" and b[0]=="G")):
        return APP_GC
    elif((a[0]=="G" and b[0]=="U") or (a[0]=="U" and b[0]=="G")):  
    	return APP_GU  
    else:
    	return 0

# The classical min function
def min(list):
    min=list[0]
    for x in list:
       if x<min:
           min=x
    return min

# The classical max function
def max(list):
    max=list[0]
    for x in list:
       if max < x:
           max=x
    return max

# Compute the matrix using the Nussinov algorithm
# On remplit d'abord la 1ere diagonale superieure, puis celle du dessus et ainsi de suite
def Mat(seq):
	T=len(seq)
	mat=numpy.zeros((T,T)) # Create Matrix
	
	for p in range(1,T,1): # On parcourt toute la sequence
            if p==1: # Calcul special pour la premiere diagonale
                for j in range(1,T,1):
                    mat[j-1,j]=Match2(seq[j-1],seq[j]) # Valeur de l'appariement entre deux voisins	
            else : # Pour le reste de la matrice
                for j in range(p,T,1):
                    A=mat[j-p,j-1]
                    B=mat[j-p+1,j]
                    C=Match2(seq[j-p],seq[j])+mat[j-p+1,j-1]
                    list=[]
                    for k in range(j-p+1,j,1): # On cherche la meilleure transfo D
			list.append(mat[j-p,k]+mat[k+1,j])
                    D=min(list)
                    mat[j-p,j]=min([A,B,C,D]) # On prend la transfo de plus haute energie d'appariement
	return mat

# Fonction reccursive qui retrouve le chemin emprunte pour remplir la derniere case	
def TraceBack(mat,seq,indi,indj):	
        T=len(seq)
	way=[]
	way.append([indi,indj,mat[indi,indj],0]) # On commence a la case donnee en parametre
	j=indj
	i=indi
	while(j>0 and i<j): # On se deplace dans la matrice superieure
                            # On ne peut que aller vers les i croissants et les j decroissants

                # Calcul du passage de l'etape i a l'etape i+1
		dep=mat[i,j]

		A=mat[i,j-1] # Valeur de la transfo A
		B=mat[i+1,j] # Valeur de la transfo B
		C=mat[i+1,j-1]+Match2(seq[i],seq[j]) # Valeur de la transfo C avec appariement seq[i] seq[j]           
                D=0 # En attente de calcul

                kl=1 # Valeur de l'ecart minimal entre deux bases pour une transfo D
                D=mat[i,i+1]+mat[i+1,j] # Valeur par defaut qui va etre soumise a la comparaison avec tous les cas possibles
                for k in range(j-i-1,1,-1): # On se deplace dans la matrice
                    if((mat[i,i+k]+mat[i+k,j])== dep): # Test si la transfo D est possible pour ce "k"
                        D=mat[i,i+k]+mat[i+k,j] # sauvegarde de D
                        kl=k # On privilegiera toujours le plus grand "k" (recouvrement plus large de la sequence)

                # On definit suivant les regles de Nussinov, les deplacements prioritaires dans la matrice
                if(dep == B): # On descend d'une case
                    way.append([i+1,j,mat[i+1,j],"B",0])
                    i=i+1                    
                elif(dep == A): # On se deplace d'une case vers la gauche
                    way.append([i,j-1,mat[i,j-1],"A",0])
                    j-=1
                elif(dep == C): # seq[i] et seq[j] sont appariees, on descend d'une case en diagonale
                    way.append([i+1,j-1,mat[i+1,j-1],"C",0])
                    i+=1
                    j-=1
                elif(dep == D): 
                    way.append([i+kl,j,mat[i+kl,j],"D",kl]) # On descend de "kl" cases 
                    way.append(TraceBack(mat,seq,i,i+kl)) # On relance la TB sur la meme ligne mais sur la colonne "i+kl"
                    i=i+kl                

	return way

# Fonctions qui permettent de transformer une liste de listes de chemin en une simple liste de way
def modifway(way):
    list=[]
    for x in way:
        if(type(x[0])==type(list)):
            for y in x:
                list.append(y)
        else:
            list.append(x)
    return list

def modifliste(way):
    list=[]
    list=modifway(way)
    cpt=len(list)
    while(cpt!=0):
        cpt=len(list)
        for i in range(len(list)):
            if(type(list[i][0])==type(1)):
                cpt-=1
                list=modifway(list)
    return list

        

# Fonction qui retrouve la transfo qui a eu lieu entre 2 cases
def Transfo(u,v):
        if(u[0]==(v[0]-1) and u[1]==(v[1]+1)):
		return "C"
	elif(u[0]==(v[0]-1) and u[1]==v[1]):
		return "B"
	elif(u[0]==v[0] and u[1]==(v[1]+1)):
		return "A"
	else:
		return "D"

# Function to save results in a file.
def save(bp):
    f=open("Pairings.txt","w")
    list=[]
    for i in range(len(bp)): # On ajoute tous les appariements possible ainsi que les bases seules
        str=[]
        if(len(bp[i])==4):
            if(bp[i][3]-bp[i][0] > TAILLE_BOUCLE): # On a considere jusque la tous les appariements possibles sans tenir compte de la limite de taille.
                list.append([bp[i][0],bp[i][1],bp[i][2],bp[i][3]])
            else: # Si les bases sont trop proches elles ne s'associent pas et sont donc non appariees
                list.append([bp[i][0],bp[i][1]])
                list.append([bp[i][3],bp[i][2]])
        elif (len(bp[i])==2):
            list.append([bp[i][0],bp[i][1]])

    ordre=0
    print list # On va ordonner les bases par positions croissantes dans le fichier rendu
    for i in range(len(list)):
        if(len(list[i])==4):
            str="%d "%list[i][0]+"%s"%list[i][1]+" %s"%list[i][2]+" %d"%list[i][3]+"\n"
        else:
            str="%d "%list[i][0]+"%s"%list[i][1]+"\n"
        f.writelines(str)
    print ordre,list[ordre]
    #    ordre+=1

    

# Fonction recursive qui retourne les bases qui sont appariees dans la structure de plus basse energie
def BasePairs(seq,way):		
	bp=[]

        for i in range(len(way)-1):
            t=way[i+1][3] # Valeur de la transfo
            val=way[i][2] # Valeur de la case correspondante dans la matrice
            
            if t=="C" and val!=0: # seq[i] et seq[j] sont appariees
                bp.append([way[i][0],seq[way[i][0]],seq[way[i][1]],way[i][1]])
            if t=="D":
                ind1=i+1
                ind2=ind1+1
                val2=1
                while val2!=0:
                    val2=way[ind2][2]
                    ind2+=1

                print way[ind1][4]
                print way[ind1],way[ind2]
                if way[ind2][3]=="C":                   
                    bp.append([way[ind1][0],seq[way[ind1][0]],seq[way[ind1][1]],way[ind1][1]])               

        # Il reste a ajouter dans la liste les bases non appariees
        list_temp=[]
        for i in range(len(bp)): # On enregistre toutes les bases appariees
            list_temp.append(bp[i][0])
            list_temp.append(bp[i][3])

        for j in range(len(seq)):
            if(list_temp.count(j)==0): # On rajoute celles qui ne sont pas encore dans la liste
                bp.append([j,seq[j]])
	return bp

# Fonction qui remet en ordre croissant le resultat de BasePairs
def tri_liste(bp):
    list=[]
    tmp=[]
    for i in range(len(bp)):
        tmp.append(bp[i][0])
    tmp.sort()

    for j in range(len(tmp)):
        for i in range(len(bp)):
            if(bp[i][0]==tmp[j]):
                list.append(bp[i])

    return list
        

			
		
#################
#               #
# Tests         #
#               #
#################

f=open("seq_1.dat","r")
seq=f.readline()
seq=seq.rstrip('\n')
print seq,len(seq)
print Mat(seq)
TB=TraceBack(Mat(seq),seq,0,len(seq)-1)
way=modifliste(TB)
BP=BasePairs(seq,way)
BPt=tri_liste(BP)
print "\nway : \n",way
print "\nAssociations : \n",BP
print "\nTri liste : \n",BPt
save(BPt)
