
d={p,s->new HashSet((0..7).collect{p[it]+s*it}).size()}
print((1..8).permutations().findAll{d(it,1)+d(it,-1)==16}) 
 
