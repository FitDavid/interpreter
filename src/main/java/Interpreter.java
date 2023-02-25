/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

/**
 *
 * @author David
 */
abstract class Stm {
}

class CompoundStm extends Stm {
    public Stm stm1;
    public Stm stm2;
    public CompoundStm(Stm s1, Stm s2) {
	stm1 = s1;
	stm2 = s2;
    }
}

class AssignStm extends Stm {
    public String id;
    public Exp exp;
    public AssignStm(String i, Exp e) {
	id = i;
	exp = e;
    }
}

class PrintStm extends Stm {
    public ExpList exps;
    public PrintStm(ExpList e) {
	exps = e;
    }
}

abstract class Exp {
}

class IdExp extends Exp {
    public String id;
    public IdExp(String i) {
	id = i;
    }
}

class NumExp extends Exp {
    public int num;
    public NumExp(int n) {
	num = n;
    }
}

enum oper { Plus , Minus , Times , Div }
class OpExp extends Exp {
    public Exp left, right;
    public oper op;
    public OpExp(Exp l, Exp r, oper o) {
	left = l;
	right = r;
	op = o;
    }
}

class EseqExp extends Exp {
    public Stm stm;
    public Exp exp;
    public EseqExp(Stm s, Exp e) {
	stm = s;
	exp = e;
    }
}

abstract class ExpList {
    public Exp head;
}

class PairExpList extends ExpList {
    public ExpList tail;
    public PairExpList(Exp h, ExpList t) {
	head = h;
	tail = t;
    }
}

class LastExpList extends ExpList {
    public LastExpList(Exp h) {
	head = h;
    }
}

class Table {
    String id;
    int value;
    Table tail;
    Table(String i, int v, Table t) {
	id = i;
	value = v;
        System.out.println("Debug value " + value);
	tail = t;
    }
    int lookup(String key) {
	if (id.equals(key)) {
	    return value;
	}
	if(tail==null)
	    System.out.println("Error lookup");
	return tail.lookup(key);
    }
}


public class Interpreter {

    public static void main(String[] args) {
	//final String input = "a := 5 + 3 ; b := ( print ( a , a - 1 ) , 10 * a ) ; print ( b )";
	NumExp num5 = new NumExp(5);
	NumExp num3 = new NumExp(3);
	OpExp opexp53 = new OpExp(num5, num3, oper.Plus);
	AssignStm assignstm1 = new AssignStm("a", opexp53);

	NumExp num1 = new NumExp(1);
	IdExp idexpa = new IdExp("a");
	OpExp opexpa1 = new OpExp(idexpa, num1, oper.Minus);
	LastExpList lastexplista1 = new LastExpList(opexpa1);
	PairExpList pairexplistaa1 = new PairExpList(idexpa, lastexplista1);
	PrintStm printstmaa1 = new PrintStm(pairexplistaa1);
	NumExp num10 = new NumExp(10);
	OpExp opexp10a = new OpExp(num10, idexpa, oper.Times);
	EseqExp eseqexp = new EseqExp(printstmaa1, opexp10a);
	AssignStm assignstm2 = new AssignStm("b", eseqexp);

	IdExp idexpb = new IdExp("b");
	LastExpList lastexplistb = new LastExpList(idexpb);
	PrintStm printstmb = new PrintStm(lastexplistb);

	Stm prog = new CompoundStm(assignstm1, new CompoundStm(assignstm2, printstmb));
	Table ti = new Table("", 0, null);
	//PrintStm prog = new PrintStm(new LastExpList(new NumExp(5)));
	//Stm prog = new CompoundStm(new AssignStm("a", opexp53), new PrintStm(new LastExpList(new IdExp("a"))));
	interpStm(prog, ti);
    }

    static Table interpStm(Stm s, Table t) {
	if (s instanceof AssignStm) {
	    AssignStm as = (AssignStm) s;
	    Table t1 = interpExp(as.exp, t);
	    Table t2 = new Table(as.id, t1.value, t1);
            System.out.println("Debug t2.id " + t2.id + " t2.value " + t2.value);
	    return t2;
	}else if(s instanceof PrintStm) {
	    PrintStm ps = (PrintStm) s;
	    printExpList(ps.exps, t);
	    return t;
	}else if(s instanceof CompoundStm){
	    CompoundStm cs = (CompoundStm) s;
	    Table t1 = interpStm(cs.stm1, t);
	    Table t2 = interpStm(cs.stm2, t1);
	    return t2;
	}
	return t; // won't be reached
    }

    static Table interpExp(Exp e, Table t) {
	if(e instanceof NumExp){
	    NumExp ne = (NumExp)e;
            System.out.println("Debug ne.num " + ne.num);
	    return new Table("",ne.num, t);
	}else if(e instanceof IdExp){
	    IdExp ie = (IdExp)e;
	    int value = t.lookup(ie.id); 
	    return new Table("", value, t);
	}else if(e instanceof OpExp){
	    OpExp oe = (OpExp)e;
	    Table t1 = interpExp(oe.left, t);
	    Table t2 = interpExp(oe.right, t1);
	    int value = execute(oe, t2);
	    return new Table("", value, t2);	
	}else if(e instanceof EseqExp){
	    EseqExp ese = (EseqExp)e;
	    Table t1 = interpStm(ese.stm, t);
	    return interpExp(ese.exp, t1);
	}
	return new Table("",0,null);
    }

    static void printExpList(ExpList el, Table t) {
	Table tv = interpExp(el.head, t);
	System.out.print(tv.value + ", ");
	if (el instanceof PairExpList) {
	    PairExpList pel = (PairExpList)el;
	    printExpList(pel.tail, tv);
	}
	else {
	    System.out.println();
	}
    }

    static int execute(OpExp oe, Table t){
	switch(oe.op){
	    case Plus:
		return t.value + t.tail.value;
	    case Minus:
		return t.value - t.tail.value;
	    case Times:
		return t.value * t.tail.value;
	    case Div:
		return t.value / t.tail.value;
	}
	return -1; 
    }
}
