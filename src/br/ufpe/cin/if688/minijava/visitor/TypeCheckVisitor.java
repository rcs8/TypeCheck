package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.And;
import br.ufpe.cin.if688.minijava.ast.ArrayAssign;
import br.ufpe.cin.if688.minijava.ast.ArrayLength;
import br.ufpe.cin.if688.minijava.ast.ArrayLookup;
import br.ufpe.cin.if688.minijava.ast.Assign;
import br.ufpe.cin.if688.minijava.ast.Block;
import br.ufpe.cin.if688.minijava.ast.BooleanType;
import br.ufpe.cin.if688.minijava.ast.Call;
import br.ufpe.cin.if688.minijava.ast.ClassDeclExtends;
import br.ufpe.cin.if688.minijava.ast.ClassDeclSimple;
import br.ufpe.cin.if688.minijava.ast.False;
import br.ufpe.cin.if688.minijava.ast.Formal;
import br.ufpe.cin.if688.minijava.ast.Identifier;
import br.ufpe.cin.if688.minijava.ast.IdentifierExp;
import br.ufpe.cin.if688.minijava.ast.IdentifierType;
import br.ufpe.cin.if688.minijava.ast.If;
import br.ufpe.cin.if688.minijava.ast.IntArrayType;
import br.ufpe.cin.if688.minijava.ast.IntegerLiteral;
import br.ufpe.cin.if688.minijava.ast.IntegerType;
import br.ufpe.cin.if688.minijava.ast.LessThan;
import br.ufpe.cin.if688.minijava.ast.MainClass;
import br.ufpe.cin.if688.minijava.ast.MethodDecl;
import br.ufpe.cin.if688.minijava.ast.Minus;
import br.ufpe.cin.if688.minijava.ast.NewArray;
import br.ufpe.cin.if688.minijava.ast.NewObject;
import br.ufpe.cin.if688.minijava.ast.Not;
import br.ufpe.cin.if688.minijava.ast.Plus;
import br.ufpe.cin.if688.minijava.ast.Print;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.ast.This;
import br.ufpe.cin.if688.minijava.ast.Times;
import br.ufpe.cin.if688.minijava.ast.True;
import br.ufpe.cin.if688.minijava.ast.Type;
import br.ufpe.cin.if688.minijava.ast.VarDecl;
import br.ufpe.cin.if688.minijava.ast.While;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;

public class TypeCheckVisitor implements IVisitor<Type> {

	private SymbolTable symbolTable;
	private Class atClass;
	private Method atMethod;
	private boolean fMethod;
	private boolean fVariable;
	
	
	public TypeCheckVisitor(SymbolTable st) {
		this.symbolTable = st;
		this.atClass = null;
		this.atMethod = null;
		this.fMethod = false;
		this.fVariable = false;
	}

	// MainClass m;
	// ClassDeclList cl;
	public Type visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i1,i2;
	// Statement s;
	public Type visit(MainClass n) {
		this.atClass = this.symbolTable.getClass(n.i1.toString());
		this.atMethod = this.symbolTable.getMethod("main", atClass.getId());
		n.i1.accept(this);
		this.fVariable = true;
		n.i2.accept(this);
		this.fVariable = false;
		n.s.accept(this);

		this.atMethod = null;
		this.atClass = null;
		
		return null;
	}
	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclSimple n) {
		this.atClass = this.symbolTable.getClass(n.i.toString());
		n.i.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		
		this.atClass = null;
		
		return null;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclExtends n) {
		this.atClass = this.symbolTable.getClass(n.i.toString());
		n.i.accept(this);
		n.j.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		
		this.atClass = null;
		
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(VarDecl n) {
		Type t = n.t.accept(this);
		this.fVariable = true;
		n.i.accept(this);
		this.fVariable = false;
		return t;
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Type visit(MethodDecl n) {
		this.atMethod = this.symbolTable.getMethod(n.i.toString(), this.atClass.getId());
		Type t = n.t.accept(this);
		this.fMethod = true;
		n.i.accept(this);
		this.fMethod = false;
		
		for (int i = 0; i < n.fl.size(); i++) {
			n.fl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		Type e = n.e.accept(this);
		
		if(!(this.symbolTable.compareTypes(t, e))) {
			System.err.println("Tipo da expressao eh diferente do tipo do metodo.");
		}
		
		this.atMethod = null;
		
		return t;
		
	}

	// Type t;
	// Identifier i;
	public Type visit(Formal n) {
		Type t = n.t.accept(this);
		fVariable = true;
		n.i.accept(this);
		fVariable = false;
		return t;
	}

	public Type visit(IntArrayType n) {
		return n;
	}

	public Type visit(BooleanType n) {
		return n;
	}

	public Type visit(IntegerType n) {
		return n;
	}

	// String s;
	public Type visit(IdentifierType n) {
		if(!this.symbolTable.containsClass(n.s)) {
			System.err.println("Tipo da classe nao foi encontrado.");
		}
		return n;
	}

	// StatementList sl;
	public Type visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Type visit(If n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof BooleanType)) {
			System.err.println("Exp nao pertence ao tipo boolean. (IF)");
		}
		
		n.s1.accept(this);
		n.s2.accept(this);
		
		return null;
	}

	// Exp e;
	// Statement s;
	public Type visit(While n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof BooleanType)) {
			System.err.println("Exp nao pertence ao tipo boolean. (WHILE)");
		}
		
		n.s.accept(this);
		
		return null;
	}

	// Exp e;
	public Type visit(Print n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	// Exp e;
	public Type visit(Assign n) {
		Type i = this.symbolTable.getVarType(this.atMethod, this.atClass, n.i.s);
		this.fVariable = true;
		n.i.accept(this);
		this.fVariable = false;
		Type e = n.e.accept(this);
		
		if(!this.symbolTable.compareTypes(i, e)) {
			System.err.println("Tipos de id e exp sao diferentes.");
		}
		
		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Type visit(ArrayAssign n) {
		this.fVariable = true;
		Type i = n.i.accept(this);
		this.fVariable = false;
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(i instanceof IntArrayType)) {
			System.err.println("O identifier nao pertence ao tipo IntArray. (Array Assign)");
		}
		if(!(e1 instanceof IntegerType)) {
			System.err.println("A exp 1 nao pertence ao tipo Int. (Array Assign)");
		}
		if(!(e2 instanceof IntegerType)) {
			System.err.println("A exp nao pertence ao tipo Int. (Array Assign)");
		}
		
		return null;
	}

	// Exp e1,e2;
	public Type visit(And n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof BooleanType)) {
			System.err.println("Exp nao pertence ao tipo boolean (And)");
		}
		
		if (!(e2 instanceof BooleanType)) {
			System.err.println("Exp nao pertence ao tipo boolean (And)");
		}
		
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(LessThan n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println("Exp 1 nao pertence ao tipo Int (LessThan)");
		}
		
		if (!(e2 instanceof IntegerType)) {
			System.err.println("Exp 2 nao pertence ao tipo Int (LessThan)");
		}
		
		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(Plus n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println("Exp 1 nao pertence ao tipo Int (Plus)");
		}
		
		if (!(e2 instanceof IntegerType)) {
			System.err.println("Exp 2 nao pertence ao tipo Int (Plus)");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Minus n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println("Exp 1 nao pertence ao tipo Int (Minus)");
		}
		
		if (!(e2 instanceof IntegerType)) {
			System.err.println("Exp 2 nao pertence ao tipo Int (Minus)");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Times n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if(!(e1 instanceof IntegerType)) {
			System.err.println("Exp 1 nao pertence ao tipo Int (Times)");
		}
		
		if (!(e2 instanceof IntegerType)) {
			System.err.println("Exp 2 nao pertence ao tipo Int (Times)");
		}
		
		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(ArrayLookup n) {
		Type e1 = n.e1.accept(this);
		Type e2 = n.e2.accept(this);
		
		if (!(e1 instanceof IntArrayType)) {
			System.err.println("Exp 1 nao pertence ao tipo IntArray (ArrayLookup)");
		}
		if (!(e2 instanceof IntegerType)) {
			System.err.println("Exp 2 nao pertence ao tipo Int (ArrayLookup)");
		}
		
		return new IntegerType();
	}

	// Exp e;
	public Type visit(ArrayLength n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof IntArrayType)) {
			System.err.println("Exp nao pertence ao tipo IntArray (ArrayLength)");
		}
		
		return new IntegerType();
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Type visit(Call n) {
		Type ct = n.e.accept(this);
		
		if(ct instanceof IdentifierType) {
			
			Class c = this.symbolTable.getClass(((IdentifierType) ct).s);
			Method m = this.symbolTable.getMethod(n.i.toString(), c.getId());
			
			Class aux = this.atClass;
			this.atClass = c;
			
			this.fMethod = true;
			Type t = n.i.accept(this);
			this.fMethod = false;
			
			atClass = aux;
			
			if (n.el.size() == countParams(m)) {
				for (int i = 0; i < n.el.size(); i++) {
					Type el = n.el.elementAt(i).accept(this);
					if(!this.symbolTable.compareTypes(el, m.getParamAt(i).type())) {
						System.err.println("Tipos nao compativeis no metodo (Call)");
					}
				}
			}else {
				System.err.println("Quantidade de parametros diferente da lista (Call)");
			}

			return t;
			
		} else {
			System.out.println("Classe nao foi encontrada (Call)");
		}
		
		return null;
		
	}

	// int i;
	public Type visit(IntegerLiteral n) {
		return new IntegerType();
	}

	public Type visit(True n) {
		return new BooleanType();
	}

	public Type visit(False n) {
		return new BooleanType();
	}

	// String s;
	public Type visit(IdentifierExp n) {
		Type i = this.symbolTable.getVarType(this.atMethod, this.atClass, n.s);
		if(i == null) {
			System.err.println("Id nao foi encontrado (IdentifierExp)");
		}
		
		return i;
	}

	public Type visit(This n) {
		return this.atClass.type();
	}

	// Exp e;
	public Type visit(NewArray n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof IntegerType)) {
			System.err.println("Exp nao pertence ao tipo Int (NewArray)");
		}
		
		return new IntArrayType();
	}

	// Identifier i;
	public Type visit(NewObject n) {
		return n.i.accept(this);
	}

	// Exp e;
	public Type visit(Not n) {
		Type e = n.e.accept(this);
		
		if(!(e instanceof BooleanType)) {
			System.err.println("Exp nao pertence ao tipo Boolean (Not)");
		}
		
		return new BooleanType();
	}

	// String s;
	public Type visit(Identifier n) {
		if (this.fMethod) {
			if(this.symbolTable.getMethod(n.toString(), this.atClass.getId()) == null) {
				System.err.println("Metodo nao foi encontrado (Identifier)");
			} else { 
				return this.symbolTable.getMethodType(n.toString(), this.atClass.getId());
			}

		} else if (this.fVariable){
			if (this.symbolTable.getVarType(this.atMethod, this.atClass, n.toString()) == null) {
				System.err.println("Variavel nao foi encontrada (Identifier)");
			} else {
				return this.symbolTable.getVarType(this.atMethod, this.atClass, n.toString());
			}
		}else {
			if(this.symbolTable.getClass(n.toString()) == null) {
				System.err.println("Classe nao foi encontrada (Identifier)");
			}
			return this.symbolTable.getClass(n.toString()).type();
		}
		
		return null;
			
	}
	
	public int countParams(Method m) {
		int count = 0;
		while (true) {
			if(m.getParamAt(count) == null) {
				break;
			}
			count ++;
		}
		return count;
	}
	
	public String teste(Type t) {
		if (t instanceof BooleanType)
			return "BooleanType";
		else if (t instanceof IdentifierType)
			return ((IdentifierType) t).s;
		else if (t instanceof IntArrayType)
			return "IntArrayType";
		else if (t instanceof IntegerType)
			return "IntegerType";
		else
			return "Null";
	}
	
	
}
