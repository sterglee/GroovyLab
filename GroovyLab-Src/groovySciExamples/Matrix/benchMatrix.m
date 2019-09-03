tic;
NLoop=1000;
N=200;
m = rand(N, N);

for reps = 1:NLoop,
  for r = 1:N,
   for  c = 1:N,
      m(r,c) = NLoop*r*c;
   end;
  end;
 end;
 
  delay = toc;
  
  disp('delay ='+delay);

  % delay ~ 77.78 sec