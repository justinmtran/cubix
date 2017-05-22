/*
	0 - Island Stage 
	1 - Snow Stage 		
	2 - Haunted Stage 
	
	Tile Type: 
	0 - Terrain Tile
	1 - Red Tile
	2 - Orange Tile
	3 - Blue Tile
	4 - Green Tile
	5 - Yellow Tile
	6 - White Tile
	7 - Start Tile
	8 - End Tile
	9 - Ice Tile

*/

var JavaPackages = new JavaImporter
(
	Packages.Tile
);
with(JavaPackages){
	function createStageGrid(grid, stageSelection){
		switch(stageSelection){
			case 0:{ // Island Stage: 8x8 
				// ROW 0
				grid[0][0].setTileType(0); grid[0][1].setTileType(0); grid[0][2].setTileType(0); grid[0][4].setTileType(0); grid[0][5].setTileType(0); grid[0][6].setTileType(4); grid[0][7].setTileType(8);
				grid[1][0].setTileType(0); grid[1][1].setTileType(0); grid[1][4].setTileType(0); grid[1][5].setTileType(0); grid[1][6].setTileType(6); 
				grid[2][0].setTileType(0); grid[2][2].setTileType(5); grid[2][3].setTileType(4); grid[2][6].setTileType(3); grid[2][7].setTileType(1); 
				grid[3][1].setTileType(3); grid[3][2].setTileType(2); grid[3][3].setTileType(2); grid[3][4].setTileType(6); grid[3][7].setTileType(5);  
				grid[4][1].setTileType(6); grid[4][4].setTileType(3); grid[4][6].setTileType(3); grid[4][7].setTileType(2);
				grid[5][0].setTileType(7); grid[5][1].setTileType(4); grid[5][3].setTileType(2); grid[5][4].setTileType(5); grid[5][5].setTileType(1); grid[5][6].setTileType(6); 
				grid[6][1].setTileType(0); grid[6][2].setTileType(0); grid[6][3].setTileType(4); grid[6][5].setTileType(4); grid[6][7].setTileType(0); 
				grid[7][0].setTileType(0); grid[7][1].setTileType(0); grid[7][2].setTileType(0); grid[7][3].setTileType(1); grid[7][4].setTileType(5); grid[7][5].setTileType(2); grid[7][6].setTileType(0); grid[7][7].setTileType(0);
				break;
			}
			case 1:{ // Snow Stage (10x10)
				grid[0][1].setTileType(0); grid[0][2].setTileType(0); grid[0][5].setTileType(0); grid[0][8].setTileType(0); grid[0][9].setTileType(0); 
				grid[1][2].setTileType(0); grid[1][3].setTileType(6); grid[1][4].setTileType(3); grid[1][6].setTileType(9); grid[1][7].setTileType(2); grid[1][8].setTileType(4); grid[1][9].setTileType(0); 
				grid[2][0].setTileType(3); grid[2][1].setTileType(7); grid[2][2].setTileType(4); grid[2][3].setTileType(2); grid[2][4].setTileType(9); grid[2][6].setTileType(3); grid[2][7].setTileType(6); grid[2][8].setTileType(6); grid[2][9].setTileType(1); 
				grid[3][0].setTileType(9); grid[3][4].setTileType(2); grid[3][5].setTileType(5); grid[3][6].setTileType(1); grid[3][7].setTileType(0); grid[3][8].setTileType(9); grid[3][9].setTileType(3); 
				grid[4][0].setTileType(5); grid[4][1].setTileType(1); grid[4][6].setTileType(9); grid[4][7].setTileType(0); grid[4][8].setTileType(6); grid[4][9].setTileType(2); 
				grid[5][0].setTileType(9); grid[5][1].setTileType(4); grid[5][2].setTileType(4); grid[5][3].setTileType(1); grid[5][4].setTileType(0); grid[5][8].setTileType(4);
				grid[6][1].setTileType(2); grid[6][2].setTileType(6); grid[6][3].setTileType(6); grid[6][4].setTileType(0); grid[6][8].setTileType(5); grid[6][9].setTileType(2); 
				grid[7][0].setTileType(0); grid[7][3].setTileType(9); grid[7][4].setTileType(9); grid[7][5].setTileType(5); grid[7][6].setTileType(4); grid[7][7].setTileType(9); grid[7][8].setTileType(6); grid[7][9].setTileType(9); 
				grid[8][0].setTileType(0); grid[8][1].setTileType(0); grid[8][2].setTileType(6); grid[8][3].setTileType(9); grid[8][4].setTileType(2); grid[8][8].setTileType(8); grid[8][9].setTileType(9); 
				grid[9][0].setTileType(0); grid[9][2].setTileType(4); grid[9][3].setTileType(2); grid[9][8].setTileType(0); grid[9][9].setTileType(0); 
				break; 
			}
			case 2:{ // Haunted Stage (12x12)
				grid[0][0].setTileType(0); grid[0][1].setTileType(0); grid[0][4].setTileType(4); grid[0][5].setTileType(6); grid[0][6].setTileType(3); grid[0][7].setTileType(0); grid[0][8].setTileType(0); 
				grid[1][0].setTileType(0); grid[1][2].setTileType(1); grid[1][3].setTileType(5); grid[1][4].setTileType(2); grid[1][6].setTileType(2); grid[1][7].setTileType(5); grid[1][11].setTileType(0); 
				grid[2][0].setTileType(4); grid[2][1].setTileType(6); grid[2][2].setTileType(3); grid[2][7].setTileType(4); grid[2][8].setTileType(1); grid[2][9].setTileType(3); grid[2][10].setTileType(2); grid[2][11].setTileType(0); 
				grid[3][0].setTileType(2); grid[3][4].setTileType(0); grid[3][10].setTileType(6); 
				grid[4][0].setTileType(3); grid[4][1].setTileType(6); grid[4][8].setTileType(0); grid[4][10].setTileType(1); grid[4][11].setTileType(4); 
				grid[5][1].setTileType(7); grid[5][2].setTileType(4); grid[5][3].setTileType(2); grid[5][6].setTileType(2); grid[5][7].setTileType(6); grid[5][8].setTileType(0); grid[5][11].setTileType(5); 
				grid[6][1].setTileType(5); grid[6][7].setTileType(3); grid[6][8].setTileType(1); grid[6][9].setTileType(4); grid[6][10].setTileType(8); grid[6][11].setTileType(3); 
				grid[7][1].setTileType(2); grid[7][2].setTileType(4); grid[7][3].setTileType(1); grid[7][5].setTileType(0); grid[7][10].setTileType(1); 
				grid[8][3].setTileType(6); grid[8][5].setTileType(0); grid[8][9].setTileType(5); grid[8][10].setTileType(4); 
				grid[9][0].setTileType(0); grid[9][3].setTileType(2); grid[9][5].setTileType(5); grid[9][6].setTileType(2); grid[9][7].setTileType(6); grid[9][9].setTileType(2); grid[9][11].setTileType(0); 
				grid[10][0].setTileType(0); grid[10][1].setTileType(0); grid[10][3].setTileType(5); grid[10][4].setTileType(3); grid[10][5].setTileType(3); grid[10][7].setTileType(3); grid[10][8].setTileType(3); grid[10][9].setTileType(6); grid[10][10].setTileType(0); grid[10][11].setTileType(0); grid[10][0].setTileType(0); grid[10][0].setTileType(0);
				grid[11][0].setTileType(0); grid[11][1].setTileType(0); grid[11][2].setTileType(0); grid[11][4].setTileType(1); grid[11][5].setTileType(6); grid[11][7].setTileType(5); grid[11][8].setTileType(1); grid[11][9].setTileType(0); grid[11][10].setTileType(0); grid[11][11].setTileType(0); 
				break; 
			}
		}
	}
}