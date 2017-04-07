var JavaPackages = new JavaImporter
(
	Packages.java.awt.Color,
	Packages.GhostAvatar
);
with(JavaPackages)
{
	var ghostColor = java.awt.Color.RED;
	
	function updateGhost(ghost)
	{
		ghost.setColor(ghostColor);
	}
}