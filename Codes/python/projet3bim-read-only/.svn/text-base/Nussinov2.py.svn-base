import numpy
APP_AU=-3
APP_GC=-4
APP_GU=-2
TAILLE_BOUCLE=3

# Matrice de score des appariements possibles
def Match2(a,b):
    if((a[0]=="A" and b[0]=="U") or (a[0]=="U" and b[0]=="A")):
        return APP_AU
    elif((a[0]=="G" and b[0]=="C") or (a[0]=="C" and b[0]=="G")):
        return APP_GC
    elif((a[0]=="G" and b[0]=="U") or (a[0]=="U" and b[0]=="G")):  
    	return APP_GU  
    else:
    	return 0

# La classique fonction min
def min(list):
    min=list[0]
    for x in list:
       if x<min:
           min=x
    return min

# La classique fonction max
def max(list):
    max=list[0]
    for x in list:
       if max < x:
           max=x
    return max

# Calcule la matrice inferieure suivant l'algorithme de Nussinov
# On remplit d'abord la 1ere diagonale superieure, puis celle du dessus et ainsi de suite
def Mat(seq):
	T=len(seq)
	mat=numpy.zeros((T,T))
	
	for p in range(1,T,1):
            if p==1:
                for j in range(1,T,1):
                    mat[j-1,j]=Match2(seq[j-1],seq[j])		
            else :
                for j in range(p,T,1):
                    A=mat[j-p,j-1]
                    B=mat[j-p+1,j]
                    C=Match2(seq[j-p],seq[j])+mat[j-p+1,j-1]
                    list=[]
                    for k in range(j-p+1,j,1):
			list.append(mat[j-p,k]+mat[k+1,j])
			D=min(list)
                    mat[j-p,j]=min([A,B,C,D])
	return mat

# Fonction qui retrouve le chemin emprunte pour remplir la derniere case	
def TraceBack(mat,seq,indi,indj):	
        T=len(seq)
	way=[]
	way.append([indi,indj,mat[indi,indj],0])
	j=indj
	i=indi
	while(j>0 and i<j): # On se deplace dans la matrice superieure
                # Calcul du passage de l'etape i a l'etape i+1
		dep=mat[i,j]

		A=mat[i,j-1]
		B=mat[i+1,j]
		C=mat[i+1,j-1]+Match2(seq[i],seq[j])            
                D=0 # En attente de calcul

                kl=1
                D=mat[i,i+1]+mat[i+1,j]
                for k in range(j-i-1,1,-1):
                    if((mat[i,i+k]+mat[i+k,j])== dep):
                        D=mat[i,i+k]+mat[i+k,j]
                        kl=k
                        #print D,kl

                #print i,j,A,B,C,D,dep

                if(dep == B):
                    way.append([i+1,j,mat[i+1,j],"B",0])
                    i=i+1                    
                elif(dep == A):
                    way.append([i,j-1,mat[i,j-1],"A",0])
                    j-=1
                elif(dep == C):
                    way.append([i+1,j-1,mat[i+1,j-1],"C",0])
                    i+=1
                    j-=1
                elif(dep == D):
                    way.append([i+kl,j,mat[i+kl,j],"D",kl])
                    way.append(TraceBack(mat,seq,i,i+kl))
                    i=i+kl                

	return way

# Fonction qui permet de transformer une liste de listes de chemin en une simple liste de way
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

def save(bp):
    f=open("Appariement.txt","w")
    list=[]
    for i in range(len(bp)):
        str=[]
        if(len(bp[i])==4):
            str="%d "%bp[i][0]+"%s"%bp[i][1]+" %s"%bp[i][2]+" %d"%bp[i][3]+"\n"
        elif (len(bp[i])==2):
            str="%d "%bp[i][0]+"%s"%bp[i][1]+"\n" 
        f.writelines(str)
    

# Fonction qui retourne les bases qui sont appariees dans la structure de plus basse energie
def BasePairs(seq,way):		
	bp=[]

        for i in range(len(way)-1):
            t=way[i+1][3]
            val=way[i][2]
            
            if t=="C" and val!=0:
                bp.append([way[i][0],seq[way[i][0]],seq[way[i][1]],way[i][1]])
            if t=="D":
                ind1=i+1
                ind2=ind1+1
                val2=1
                while val2!=0:
                    val2=way[ind2][2]
                    ind2+=1
                #ind2+=1
                print way[ind1][4]
                print way[ind1],way[ind2]
                if way[ind2][3]=="C":# and way[i][3]!="D":
                    bp.append([way[ind1][0],seq[way[ind1][0]],seq[way[ind1][1]],way[ind1][1]])               

        # Il reste a ajouter dans la liste les bases non appariees
        list_temp=[]
        for i in range(len(bp)):
            list_temp.append(bp[i][0])
            list_temp.append(bp[i][3])

        for j in range(len(seq)):
            if(list_temp.count(j)==0):
                bp.append([j,seq[j]])
	return bp

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
# Zone de tests #
#               #
#################

#f=open("Sequence3.txt","r")
#seq=f.readline()
#seq=seq.rstrip('\n')
#print seq
#print Mat(seq)
#TB=TraceBack(Mat(seq),seq,0,len(seq)-1)
#way=modifliste(TB)
#BP=BasePairs(seq,way)
#BPt=tri_liste(BP)
#print "\nway : \n",way
#print "\nAssociations : \n",BP
#print "\nTri liste : \n",BPt
#save(BPt)
