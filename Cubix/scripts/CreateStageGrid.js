/*
	0 - Island Stage 1	 
	1 - Island Stage 2 	
	2 - Snow Stage 1 	
	3 - Snow Stage 2 	
	4 - Haunted Stage 1 
	5 - Haunted Stage 2 
	
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
				grid[0][0].setTileType(0); grid[0][1].setTileType(0); grid[0][2].setTileType(0); grid[0][4].setTileType(0); grid[0][5].setTileType(0); grid[0][6].setTileType(4); grid[0][7].setTileType(8);
				grid[1][0].setTileType(0); grid[1][1].setTileType(0); grid[1][4].setTileType(0); grid[1][5].setTileType(0); grid[1][6].setTileType(6); 
				grid[2][0].setTileType(0); grid[2][2].setTileType(5); grid[2][3].setTileType(4); grid[2][6].setTileType(3); grid[2][7].setTileType(1); 
				grid[3][1].setTileType(3); grid[3][2].setTileType(2); grid[3][3].setTileType(2); grid[3][4].setTileType(6); grid[3][7].setTileType(5);  
				grid[4][1].setTileType(6); grid[4][4].setTileType(3); grid[4][6].setTileType(3); grid[4][7].setTileType(2);
				grid[5][0].setTileType(7); grid[5][1].setTileType(4); grid[5][3].setTileType(2); grid[5][4].setTileType(5); grid[5][5].setTileType(1); grid[5][6].setTileType(6); 
				grid[6][1].setTileType(0); grid[6][2].setTileType(0); grid[6][3].setTileType(4); grid[6][5].setTileType(4); grid[6][7].setTileType(0); 
				grid[7][0].setTileType(0); grid[7][1].setTileType(0); grid[7][2].setTileType(0); grid[7][3].setTileType(1); grid[7][4].setTileType(5); grid[7][5].setTileType(2); grid[7][6].setTileType(0); grid[7][7].setTileType(0);
			}
			case 1: // Snow Stage 1
			case 2: // Haunted Stage 1
		}
	}
}