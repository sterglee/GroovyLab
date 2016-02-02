

f1 = 0.3;
f2 = 0.12;
a1 = 0.5;
a2 = 0.9;
low = -20;
high = 20;
dt=0.01;
t= inc(low, dt,  high);
x = a1*sin(f1*t)+a2*cos(f2*t);
figure(1); hold("off"); plot(t,x);
swing = new SwingBuilder()

 frame =  swing.frame(title: 'Data Entry Frame test') {
    panel() {
      panel() { label('f1'); textField(id:'f1', String.valueOf(f1), columns:10) }
      panel() { label('f2'); textField(id:'f2', String.valueOf(f2), columns:10) }
      panel() { label('a1'); textField(id:'a1', String.valueOf(a1), columns:10) }
      panel() { label('a2'); textField(id:'a2', String.valueOf(a2), columns:10) }
      button(text: 'Plot', actionPerformed: {
        f1 = Double.valueOf(swing.f1.text);
        f2 = Double.valueOf(swing.f2.text);
        a1 = Double.valueOf(swing.a1.text);
        a2 = Double.valueOf(swing.a2.text);
        clf(1); x = a1*sin(f1*t)+a2*cos(f2*t); plot(t,x);
     })
   }
 }
 
frame.pack()
frame.setVisible(true);