/* Query- Base para retornar o ranking de diversas instituições.
   Provavelmente será necessário rodar uma query destas para o país, uma para cada estado, e uma para cada município para calcular o ranking 
   Fonte: http://stackoverflow.com/questions/1895110/row-number-in-mysql
*/

/* Query que faz um ranking de acordo com outra select */


/* Opção 1

SELECT 
    @i:=@i+1 AS rank, 
    -- Aqui em baixo vai qualquer coisa
    u.*
FROM
	-- Aqui vão as tabelas de origem dos dados 
    usuario AS u,
    -- Não mudar esta parte de baixo
    (SELECT @i:=0) AS foo
ORDER BY 
	u.nome ASC -- Critério de Ordenamento
	
*/

/* Opção 2 parece melhor. 
	Para ranking descendente, fazer um count das instituições,  e definir @i = count ... + 1 e o order by ao contrário, usando @i = @i = -1;
*/
-- Ranking Ascendente
SET @i := 0;
	
SELECT 
    @i:=@i+1 AS rank, 
    -- Aqui em baixo vai qualquer coisa
    u.*
FROM
	-- Aqui vão as tabelas de origem dos dados 
    usuario AS u
    
ORDER BY 
	u.nome ASC -- Critério de Ordenamento