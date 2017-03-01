import java.util.*;

public class DECAFTypes extends DECAFBaseVisitor<String> {
	public int scope_counter;
	public Stack<Integer> current_scope;
	public Map<String, ArrayList < Symbol > > SymbolTable;
		
	public DECAFTypes(){
		scope_counter = 0;
		current_scope = new Stack<Integer>();
		SymbolTable = new HashMap<String, ArrayList < Symbol > >();
	}
		
	//Declaration Scope
	
	@Override
	public String visitProgram(DECAFParser.ProgramContext ctx){
		System.out.println("visitProgram");
		//System.out.println(ctx.getText());
		scope_counter += 1;
		current_scope.push(scope_counter);
		System.out.println("--Scope counter : " + String.valueOf(scope_counter));
		String result = visitChildren(ctx);
		current_scope.pop();
	//	System.out.println("Method Declaration Table "+MethodDeclarationTable);
		return result;

	}

	@Override
	public String visitStructDeclaration(DECAFParser.StructDeclarationContext ctx){
		System.out.println("visitStructDeclaration");
		scope_counter += 1;
		current_scope.push(scope_counter);
		System.out.println("--Scope counter : " + String.valueOf(scope_counter));
		String result = visitChildren(ctx);
		return result;

	}
	
	@Override
	public String visitMethodDeclaration(DECAFParser.MethodDeclarationContext ctx){
		System.out.println("visitMethodDeclaration");
		scope_counter += 1;
		current_scope.push(scope_counter);
		System.out.println("--Scope counter : " + String.valueOf(scope_counter));
		ArrayList<String> parameters = new ArrayList<String>();
		String signature = "";
		String id = ctx.getChild(1).getText();
		String varType = ctx.getChild(0).getText();
		//We want the production that has x parameters, so x = childCount - 5
		//The number 5 is because of the normal parameters that always appear
		if(ctx.getChildCount() > 5){
			Integer i = 0;
			while(i<ctx.getChildCount()-5){
				//Until we see "," then we can say that the signature has multiple parameters
				if(!ctx.getChild(3+i).getText().equals(",")){
					System.out.println("Parameter " + i + " " + ctx.getChild(3+i).getText());
					//Simple Parameter
					if(ctx.getChild(3+i).getChildCount() == 2){
						parameters.add(ctx.getChild(3+i).getChild(0).getText());
						signature += ctx.getChild(3+i).getChild(0).getText() + ",";
					} else { //[] Parameter
						String temp_parameter = ctx.getChild(3+i).getChild(0).getText() + ctx.getChild(3+i).getChild(2).getText() + ctx.getChild(3+i).getChild(3).getText() + ",";
						parameters.add(temp_parameter);
						signature += temp_parameter;

					}
				}
				i++;
			}
		}

		if(signature.equals("")){
			System.out.println("Method : "+ id + ", no Signature ");
		} else {
			System.out.println("Method : "+ id + ", Signature: " + signature);
		}
		id = id + signature;
		if(SymbolTable.containsKey(id)){
			SymbolTable.get(id).add(new Symbol(id, scope_counter, scope_counter, parameters, varType));
		} else {
			SymbolTable.put(id, new ArrayList< Symbol>());
			SymbolTable.get(id).add(new Symbol(id, scope_counter, scope_counter, parameters, varType));
		}
		String result = visitChildren(ctx);
		return result;
	}
	
	@Override
	public String visitVarDeclaration(DECAFParser.VarDeclarationContext ctx){
		System.out.println("visitVarDeclaration");
		String varType = ctx.getChild(0).getText();
		String id = ctx.getChild(1).getText();
		//varType ID LCORCH NUM RCORCH DOTCOMMA
		if(ctx.getChildCount() == 6){
			Integer arraySize = Integer.parseInt(ctx.getChild(3).getText());
			if(SymbolTable.containsKey(id)){
				SymbolTable.get(id).add(new Symbol(id, true, arraySize, scope_counter, scope_counter, varType));
			} else {
				SymbolTable.put(id, new ArrayList<Symbol>());
				SymbolTable.get(id).add(new Symbol(id, true, arraySize, scope_counter, scope_counter, varType));
			}
		} else { //varType ID DOTCOMMA 
			if(SymbolTable.containsKey(id)){
				SymbolTable.get(id).add(new Symbol(id, false, 0, scope_counter, scope_counter, varType));	
			} else {
				SymbolTable.put(id, new ArrayList<Symbol>());
				SymbolTable.get(id).add(new Symbol(id, false, 0, scope_counter, scope_counter, varType));
			}	
		}
		System.out.println(ctx.getText());
		String result = visitChildren(ctx);
		return result;
	}

	//Im not pretty sure if new blocks marks a new scope
	@Override
	public String visitIfBlock(DECAFParser.IfBlockContext ctx){
		System.out.println("visitIfBlock");
		scope_counter += 1;
		current_scope.push(scope_counter);
		System.out.println("--Scope counter : " + String.valueOf(scope_counter));
		String result = visitChildren(ctx);
		return result;
		
	}

	@Override
	public String visitWhileBlock(DECAFParser.WhileBlockContext ctx){
		System.out.println("visitWhileBlock");
		scope_counter += 1;
		current_scope.push(scope_counter);
		System.out.println("--Scope counter : " + String.valueOf(scope_counter));
		String result = visitChildren(ctx);
		return result;
	}

	//AddSubs and MulDiv operations

	@Override
	public String visitAddSubsExpression(DECAFParser.AddSubsExpressionContext ctx){
		System.out.println("visitAddSubsExpression");
		//System.out.println(String.valueOf(ctx.getChildCount()));
		if(ctx.getChildCount() == 3){
			String addSubsExpression = visitChildren(ctx.addSubsExpression());
			String mulDivExpression = visitChildren(ctx.mulDivExpression());
			System.out.println("**addSubsExpressionType : "+addSubsExpression);
			System.out.println("**mulDivExpressionType : "+mulDivExpression);
			//Return Error if types are different
			return mulDivExpression;
		} else {
			String mulDivExpression = visitChildren(ctx.mulDivExpression());
			System.out.println("**mulDivExpressionType : "+mulDivExpression);
			return mulDivExpression;
		}
		
	}
	
	@Override
	public String visitMulDivExpression(DECAFParser.MulDivExpressionContext ctx){
		System.out.println("visitMulDivExpression");
		//System.out.println(String.valueOf(ctx.getChildCount()));
		if(ctx.getChildCount() == 3){
			String mulDivExpression = visitChildren(ctx.mulDivExpression());
			String prExpression = visitChildren(ctx.prExpression());
			System.out.println("**mulDivExpressionType : "+mulDivExpression);
			System.out.println("**prExpressionType : "+prExpression);
			//Return Error if types are different
			return prExpression;
		} else {
			String prExpression = visitChildren(ctx.prExpression());
			System.out.println("**prExpressionType : "+prExpression);
			return prExpression;
		}
	}
	
	//Integer, Char and Boolean Literals
	
	@Override
	public String visitParameterType(DECAFParser.ParameterTypeContext ctx){
		System.out.println("__visitParameterType, " + ctx.getText());
		if(ctx.getText().equals("int")){
			return "int";
		}else if(ctx.getText().equals("char")){
			return "char";
		}else if(ctx.getText().equals("boolean")){
			return "boolean";
		}
		return "Error";
	}

	@Override 
	public String visitInt_literal(DECAFParser.Int_literalContext ctx){
		System.out.println("__visitInt_literal, " + ctx.getText());
		return "int";
	}
	@Override
	public String visitChar_literal(DECAFParser.Char_literalContext ctx){
		System.out.println("__visitCharLiteral, " + ctx.getText());
		return "char";
	}
	
	@Override 
	public String visitBool_literal(DECAFParser.Bool_literalContext ctx){
		System.out.println("__visitBool_literal, " + ctx.getText());
		return "boolean";
	}
}
